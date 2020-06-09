package moods.common;

import java.io.Serializable;

public class RPCResponse implements Serializable {
    private static final long serial_id = 1L;
    private String requestId;
    private Exception error;
    private Object result;


    public boolean hasError() {
        return error != null;
    }

    public static long getSerialVersionUID() {
        return serial_id;
    }

    public String getRequestId() {
        return requestId;
    }

    public Exception getError() {
        return error;
    }

    public Object getResult() {
        return result;
    }
}
