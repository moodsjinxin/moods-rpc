package moods.cluster.loadBalancer;

import moods.cluster.LoadBalancer;
import moods.common.RPCRequest;

import java.util.List;


//轮询负载均衡
public class RoundRobinLoadBalancer implements LoadBalancer {
    private int index = 0;
    @Override
    public String select(List<String> endPoints, RPCRequest request) {
        if(endPoints.isEmpty()){ return null;}
        String current = endPoints.get(index%endPoints.size());
        index = (index+1)%endPoints.size();
        return current;
    }
}
