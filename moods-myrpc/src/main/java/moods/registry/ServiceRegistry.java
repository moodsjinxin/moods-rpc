package moods.registry;

import java.util.List;

//服务注册
public interface ServiceRegistry {
    static String REGISTRY_PATH = "/moods";
    static String PROVIDER_PATH = "/provider";
    static String CONSUMER_PATH = "/consumer";

    //发现服务列表
    List<String> discover(String service);

    //注册服务(服务名、地址)
    void registry(String service,String address);


    //（服务名、地址）构建znode地址路径
    default String getZnodePath(String service,String address){
        return REGISTRY_PATH + "/" + service + PROVIDER_PATH + "/" +address;
    }

    //获得服务地址
    default String getServicePath(String service){
        return REGISTRY_PATH + "/" + service + PROVIDER_PATH;
    }



}
