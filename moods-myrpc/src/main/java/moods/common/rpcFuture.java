package moods.common;

import moods.transport.netty.client.rpcClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class rpcFuture implements Future<Object> {

    private rpcClient client;
    private RPCRequest request;
    private RPCResponse response;

    CountDownLatch latch = new CountDownLatch(1);


    //存放所有的callback
    List<RPCCallback> callableLists = new ArrayList<RPCCallback>();

    public rpcFuture() {
    }

    public rpcFuture(RPCRequest request,rpcClient client) {
        this.request = request;
        this.client = client;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return latch.getCount() == 0;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        try {
            latch.await();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return response.getResult();
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        try {
            if(!latch.await(timeout,unit)){
                throw  new TimeoutException("rpc 请求超时");
            }
        }catch (InterruptedException e){
            System.out.println(e.getMessage());
        }
        return response.getResult();
    }

    /**
     *  存放响应结果
     */

    public void done(RPCResponse response){
        this.response = response;
        latch.countDown();

        //运行回调列表中的所有回调函数
        invokeCallback();
    }


    //运行回调列表中的所有回调函数
    private void invokeCallback(){
        for(final RPCCallback callback:callableLists){
            runCallback(callback);
        }
    }


    //执行回调函数
    private  void runCallback(final RPCCallback callback){
        final RPCResponse res = this.response;
        client.submit(() -> {
            callback.invoke(res);
        });
    }

    public rpcFuture addCallback(RPCCallback callback){
        //如果添加时有响应直接运行
        if(isDone()){
            runCallback(callback);
        }else{
            this.callableLists.add(callback);
        }
        return this;
    }
}
