package moods.registry.zookeeper;

import lombok.extern.slf4j.Slf4j;
import moods.registry.ServiceRegistry;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


@Slf4j
public class ZKServiceRegistry implements ServiceRegistry {

    private ZooKeeper zooKeeper;


    //本地缓存（map）
    private ConcurrentHashMap<String, List<String>> addressCache = new ConcurrentHashMap<>();


    /**
     * 初始化时连接Zookeeper
     * */
    ZKServiceRegistry(String registryAddress){
        connectServer(registryAddress);
    }

    private void connectServer(String registryAddress){
        try {
            //用CountDownLatch来阻塞主线程，因为ZOOKEEPER的创建是异步的
            CountDownLatch latch = new CountDownLatch(1);

            //异步实例化Zookeeper，并用watchEvent的状态来判断是否成功创建Zookeeper
            zooKeeper = new ZooKeeper(registryAddress,ZKtools.ZK_SESSION_TIMEOUT,watchedEvent -> {
                /**WATCHER invent
                 * 如果成功连接
                 * */
                if(watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected){
                    latch.countDown();
                }

            });
            //10秒未连接上报错
            boolean status =latch.await(10, TimeUnit.SECONDS);
            if(!status){
                log.error("zooKeeper连接失败！");
            }

        }catch (IOException | InterruptedException e){
            log.error("zooKeeper连接失败！");
        }

    }

    @Override
    public List<String> discover(String service) {
       if(addressCache.containsKey(service)){
            log.info("已从本地缓存中获取该服务");
            return addressCache.get(service);
       }else{
           //从Zookeeper中获取放入本地缓存中,并设置监听事件
           watchNode(service,getServicePath(service));
           return addressCache.get(service);


       }
    }


    private void watchNode(String service,String path)  {
        try {
            List<String> addres = zooKeeper.getChildren(path,watchedEvent -> {
                if(watchedEvent.getType() == Watcher.Event.EventType.NodeChildrenChanged){
                    watchNode(service,path);
                }
            });
            addressCache.put(service,addres);
        }catch (InterruptedException | KeeperException e){
            e.printStackTrace();
            log.error("从Zookeeper中获取失败！");
        }
    }
    @Override
    public void registry(String service, String address) {
        String path = getZnodePath(service,address);
        try{
            ZKtools.creatNode(zooKeeper,path);
        }catch (KeeperException |InterruptedException e ){
            e.printStackTrace();
            log.error("创建ZNODE失败，服务注册失败！");
        }

    }


}
