package moods.common.context;

import moods.common.rpcFuture;

public class RPCcontext {
    private static ThreadLocal<RPCcontext> rpccontexts = ThreadLocal.withInitial(RPCcontext::new);
    private RPCcontext(){};
    private rpcFuture future;
    public static RPCcontext getContext() {
        return rpccontexts.get();
    }

    public rpcFuture getFuture(){
        return future;
    }

    public void setFuture(rpcFuture future){
        this.future = future;
    }
}
