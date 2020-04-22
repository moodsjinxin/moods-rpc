package moods.registry.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

//ZK的工具类
public class ZKtools {
    static final int ZK_SESSION_TIMEOUT = 5000;
    //创建一个临时的NODE
    static void creatNode(ZooKeeper zooKeeper,String path) throws KeeperException, InterruptedException {
        String[] paths = path.split("/");
        StringBuilder str =new StringBuilder();

        for(int  i=0;i<paths.length;i++){
            if(!"".equals(paths[i])){
                str.append("/").append(paths[i]);
                if(zooKeeper.exists(str.toString(),false)==null){
                    //如果是前面的路径，创建永久路径
                    if(i!=paths.length-1){
                        zooKeeper.create(str.toString(),new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    }else{
                        //节点的地址创建临时的
                        zooKeeper.create(str.toString(),new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                    }
                }
            }
        }

    }
 }
