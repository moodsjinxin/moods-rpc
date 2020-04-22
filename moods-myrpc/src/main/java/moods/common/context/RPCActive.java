package moods.common.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RPCActive {
    private static final Map<String, Integer> ACTIVE_MAP = new ConcurrentHashMap<>();

    public synchronized static int getCount(String interfacename,String methodname,String address){
        String key = makeKey(interfacename,methodname,address);
        if(!ACTIVE_MAP.containsKey(key)){
            return 0;
        }
        return ACTIVE_MAP.get(key);
    }

    public synchronized static void addCount(String interfacename,String methodname,String address){
        String key = makeKey(interfacename,methodname,address);
        ACTIVE_MAP.put(key,ACTIVE_MAP.getOrDefault(key,0)+1);
    }

    public synchronized static void subCount(String interfacename,String methodname,String address){
        String key = makeKey(interfacename,methodname,address);
        ACTIVE_MAP.put(key,ACTIVE_MAP.get(key)-1);
    }

    private static String makeKey(String interfacename,String methodname,String address){
        return interfacename + "." + methodname + "." + address;
    }

}
