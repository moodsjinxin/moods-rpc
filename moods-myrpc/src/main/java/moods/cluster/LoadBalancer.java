package moods.cluster;


import moods.common.RPCRequest;

import java.util.List;

//负载均衡
public interface LoadBalancer {
    String select(List<String> endPoints, RPCRequest request);
}
