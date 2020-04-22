package moods.cluster.loadBalancer;

import moods.cluster.LoadBalancer;
import moods.common.RPCRequest;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


//随机负载均衡
public class RandomLoadBalancer implements LoadBalancer {
    @Override
    public String select(List<String> endPoints, RPCRequest request) {
        if(endPoints.isEmpty()){return null;}
        return endPoints.get(ThreadLocalRandom.current().nextInt(endPoints.size()));
    }
}
