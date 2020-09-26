package moods.transport.netty.client;

import moods.common.RPCResponse;
import moods.common.rpcFuture;

import java.util.concurrent.ConcurrentHashMap;

public class RPCFutureManager {
    private ConcurrentHashMap<String, rpcFuture> rpcFutureMap = new ConcurrentHashMap<>();
    /**
     * Singleton
     */

    private static class SingletonHolder {
        private static RPCFutureManager rpcFutureManager = new RPCFutureManager();
    }

    public static RPCFutureManager getInstance() {
        return SingletonHolder.rpcFutureManager;
    }

    public void registerFuture(rpcFuture rpcFuture) {
        rpcFutureMap.put(rpcFuture.getRequest().getRequestId(), rpcFuture);
    }

    public void futureDone(RPCResponse response) {
       rpcFuture delRpc = rpcFutureMap.remove(response.getRequestId());
       delRpc.done(response);
    }

    public rpcFuture getFuture(String requestId) {
        return rpcFutureMap.get(requestId);
    }
}
