package moods.cluster.loadBalancer;

import moods.cluster.LoadBalancer;
import moods.common.RPCRequest;
import moods.common.context.RPCActive;

import java.util.List;

/**
 * 每个服务维护一个活跃数计数器。当A机器开始处理请求，该计数器加1，此时A还未处理完成。
 * 若处理完毕则计数器减1。而B机器接受到请求后很快处理完毕。
 * 那么A,B的活跃数分别是1，0。当又产生了一个新的请求，
 * 则选择B机器去执行(B活跃数最小)，这样使慢的机器A收到少的请求。
 */
//较少活跃数
public class LeastActiveLoadBalancer implements LoadBalancer {
    @Override
    public String select(List<String> endPoints, RPCRequest request) {
            String enpoint = null;
            int least = 0;
            for(String addres : endPoints){
                int count  = RPCActive.getCount(request.getInterfaceName(),request.getMethodName(),addres);
                if(enpoint == null || count<least){
                    enpoint = addres;
                    least = count;
                }
            }
            return enpoint;
    }
}
