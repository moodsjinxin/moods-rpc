package moods.filter;

import moods.common.RPCResponse;

public abstract class AbstractAfterFilter {
    protected AbstractAfterFilter next;

    public abstract void invoke(RPCResponse response,String endpoint);
}
