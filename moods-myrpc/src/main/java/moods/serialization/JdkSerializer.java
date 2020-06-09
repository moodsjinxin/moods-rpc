package moods.serialization;

import java.io.*;

public class JdkSerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T obj) throws IOException {
       try {
           ByteArrayOutputStream byteout = new ByteArrayOutputStream();
           ObjectOutputStream jdk_out = new ObjectOutputStream(byteout);
           jdk_out.writeObject(obj);
           return  byteout.toByteArray();
       }catch (Throwable e){
           throw new IOException("JDK序列化异常");
       }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) throws IOException {
        try {
            ObjectInputStream jdk_in = new ObjectInputStream(new ByteArrayInputStream(data));
            return cls.cast(jdk_in.readObject());
        }catch (Throwable e){
            throw  new IOException("JDK反序列化失败");
        }
    }
}
