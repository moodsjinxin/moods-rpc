package moods.cluster.loadBalancer;

import lombok.extern.slf4j.Slf4j;
import moods.cluster.LoadBalancer;
import moods.common.RPCRequest;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

@Slf4j
public class HashLoadBalancer implements LoadBalancer {
    //一个真实节点对应3个虚拟节点
    private static final int VIRTUAL_NODES = 3;

    private List<String> cachedEndpoints;
    //用sortedmap实现哈希环
    private final SortedMap<Integer, String> circle = new TreeMap<>();


    @Override
    public String select(List<String> endPoints, RPCRequest request) {
        if(cachedEndpoints == null || !cachedEndpoints.contains(endPoints)){
            buildmap(endPoints);
            cachedEndpoints = endPoints;
        }
        return getServer(request.key());
    }

    private void buildmap(List<String> endPoints){
        for(int i=0;i<endPoints.size();i++){
            for(int j=0;j<VIRTUAL_NODES;j++){
                String virtualNodeName =endPoints.get(i)+"&&VN"+j;
                //构建哈希环上的虚拟服务器结点hash值
                int hash = getHash(virtualNodeName);
                log.debug("虚拟节点(" + virtualNodeName + ")被添加, hash值为" + hash);
                circle.put(hash, virtualNodeName);
            }
        }
    }


    //根据key值获取服务
    private String getServer(String key) {
        int hash = getHash(key);

        // 得到大于该hash值的所有键
        SortedMap<Integer, String> subMap = circle.tailMap(hash);

        String virtualNode;
        if (subMap.isEmpty()) {
            virtualNode = circle.get(circle.firstKey());
        } else {
            virtualNode = subMap.get(subMap.firstKey());
        }

        return virtualNode.substring(0, virtualNode.indexOf("&&"));
    }

    /**
     * FNV1_32_HASH 算法计算服务器的Hash值
     * 一致性hash算法
     * **/
    private static int getHash(String str){
        final int p = 16777619;
        int hash = (int)2166136261L;
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // 如果算出来的值为负数则取其绝对值
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }


}
