package moods.serialization;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianSerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        ByteArrayOutputStream byteout ;
        Hessian2Output hout = null;

        try{
            byteout = new ByteArrayOutputStream();
            hout = new Hessian2Output(byteout);

            hout.writeObject(obj);
            hout.flush();
            return byteout.toByteArray();
        }catch (Throwable e){
            throw new IOException("Hession序列化异常");
        }finally {
            if(hout != null){
                try {
                    hout.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) throws IOException {
        Hessian2Input hin = null;
        try {
            hin = new Hessian2Input(new ByteArrayInputStream(data));
            return cls.cast(hin.readObject(cls));
        }catch (Throwable e){
            throw new IOException("hession反序列化失败");
        }finally {
            if(hin != null){
                try {
                    hin.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }
    }
}
