package moods.common;


import java.io.Serializable;
import java.util.Arrays;

/**
 * rpc请求
 *
 **/
public class RPCRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String requestId;

    //调用的服务接口以及方法名称
    private String interfaceName;
    private String methodName;

    //调用的参数类型和参数
    private Class<?>[] parameterTypes;
    private Object[] parameters;

    //生成对应的key
    public String key() {
        return interfaceName +
                "." +
                methodName +
                "." +
                Arrays.toString(parameterTypes) +
                "." +
                Arrays.toString(parameters);
    }

}
