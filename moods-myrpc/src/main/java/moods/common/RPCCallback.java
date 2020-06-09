package moods.common;


@FunctionalInterface
public interface RPCCallback {
    void invoke(RPCResponse response);
}
