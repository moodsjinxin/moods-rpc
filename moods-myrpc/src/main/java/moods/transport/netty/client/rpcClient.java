package moods.transport.netty.client;

import io.netty.channel.Channel;
import lombok.NoArgsConstructor;
import moods.cluster.LoadBalancer;
import moods.cluster.loadBalancer.LeastActiveLoadBalancer;
import moods.common.RPCRequest;
import moods.common.context.RPCactivecount;
import moods.common.context.RPCcontext;
import moods.filter.AbstractAfterFilter;
import moods.filter.AbstractBeforeFilter;
import moods.registry.ServiceRegistry;
import moods.transport.netty.constant.InvokeType;
import moods.util.StringUtil;
import moods.filter.filterMannager;
import moods.common.rpcFuture;


import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class rpcClient {
    private LoadBalancer loadBalancer; //负载均衡算法
    private ServiceRegistry serviceRegistry; //基于zookper
    private Connectmanager connectmanager;  //基于netty


    /**
     * 客户端线程池
     */
    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16, 600L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(65536), new ThreadFactory() {
        private AtomicInteger atomicInteger = new AtomicInteger(0);
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,"Client Tread - "+ atomicInteger.getAndIncrement());
        }
    });

    public rpcClient(LoadBalancer loadBalancer,ServiceRegistry serviceRegistry,Connectmanager connectmanager){
        this.loadBalancer = loadBalancer;
        this.serviceRegistry =serviceRegistry;
        this.connectmanager = connectmanager;
    }

    public rpcClient(){}

    public void submit(Runnable a){
        threadPoolExecutor.submit(a);
    }

    public <T> T create(Class<T> serviceInterface){
        return create(serviceInterface, InvokeType.SYNC);
    }


    //uncheck
    public <T> T create(Class<T> serviceInterface,InvokeType type){
        return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(),
                new Class<?>[]{serviceInterface},((proxy, method, args) ->
                {
                    RPCRequest request = RPCRequest.builder()
                            .requestId(UUID.randomUUID().toString())
                            .interfaceName(method.getDeclaringClass().getName())
                            .methodName(method.getName())
                            .parameterTypes(method.getParameterTypes())
                            .parameters(args).build();


                    String serviceName = serviceInterface.getName();

                    //获取服务地址列表
                    List<String> endPoints =serviceRegistry.discover(serviceName);

                    //负载均衡获得一个服务地址
                    String select =loadBalancer.select(endPoints,request);

                    if (select == null){
                        throw new RuntimeException("对应服务不存在实例");
                    }

                    //获取channel
                    Channel channel = connectmanager.getChannel(StringUtil.str2socket(select));

                    //beforefilter
                    if(filterMannager.beforeFilter != null){
                        filterMannager.beforeFilter.invoke(request,select);
                    }

                    if(type == InvokeType.ONEWAY){
                        RPCcontext.getContext().setFuture(null);
                        sendRequest(channel,request);
                        return null;
                    }else if (type == InvokeType.SYNC){
                        rpcFuture rpcFuture = new rpcFuture(request,this);
                        RPCcontext.getContext().setFuture(rpcFuture);
                        RPCFutureManager.getInstance().registerFuture(rpcFuture);
                        sendRequest(channel,request);
                        //同步调用，马上用get堵塞
                        return rpcFuture.get();
                    }else{
                        rpcFuture future = new rpcFuture(request,this);
                        RPCcontext.getContext().setFuture(future);
                        RPCFutureManager.getInstance().registerFuture(future);
                        sendRequest(channel,request);
                        return null;
                    }
                }));
    }

    private void sendRequest(Channel channel,RPCRequest request) throws Exception{
        System.out.println("开始发送请求");
        CountDownLatch latch = new CountDownLatch(1);

        channel.writeAndFlush(request).addListener(
                future -> latch.countDown()
        );

        latch.await();

        InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();

        if(loadBalancer instanceof LeastActiveLoadBalancer){
            RPCactivecount.addCount(request.getInterfaceName(),request.getMethodName(),address.getAddress()+":"+address.getPort());
        }
        System.out.println("请求发送成功："+request);
    }

    public void addBeforeFilter(AbstractBeforeFilter filter) {
        filterMannager.addBeforeFilter(filter);
    }

    public void addAfterFilter(AbstractAfterFilter filter) {
        filterMannager.addAfterFilter(filter);
    }
}
