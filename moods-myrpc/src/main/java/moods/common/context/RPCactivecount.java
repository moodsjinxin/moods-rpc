package moods.common.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RPCactivecount {
    private static final Map<String,Integer> RPC_Active = new ConcurrentHashMap<String, Integer>();


    public synchronized static int getcount(String interfacename,String methodName,String address){
        String key = makekey(interfacename,methodName,address);
        if(!RPC_Active.containsKey(key)){return 0;}
        return RPC_Active.get(key);
    }

    public synchronized  static void addCount(String interfacename,String methodName,String address){
        String key = makekey(interfacename,methodName,address);
        RPC_Active.put(key,RPC_Active.getOrDefault(key,0)+1);
    }

    public synchronized  static void decCount(String interfacename,String methodName,String address){
        String key = makekey(interfacename,methodName,address);
        RPC_Active.put(key,RPC_Active.get(key)-1);
    }

    private static String makekey(String interfacename,String methodName,String address){
        return interfacename+"."+methodName+"."+address;
    }
}
