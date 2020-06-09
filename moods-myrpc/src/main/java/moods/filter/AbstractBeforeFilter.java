package moods.filter;

import moods.common.RPCRequest;

public abstract class AbstractBeforeFilter {
    protected AbstractBeforeFilter next;

    public abstract void invoke(RPCRequest request,String endpoint);
}
