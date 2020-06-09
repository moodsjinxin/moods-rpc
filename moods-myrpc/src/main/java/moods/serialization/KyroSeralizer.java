package moods.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KyroSeralizer implements Serializer {
    private static final  ThreadLocal<Kryo> thread_kryo = new ThreadLocal<Kryo>(){
            protected Kryo initiaValue(){
                Kryo kryo = new Kryo();
                kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
                return kryo;
            }
    };

    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        try(
                Output output = new Output(new ByteArrayOutputStream())){
                Kryo kry = thread_kryo.get();
                kry.writeObject(output,obj);
                return output.toBytes();
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) throws IOException {
        try (Input input = new Input(new ByteArrayInputStream(data))) {
            Kryo kryo = thread_kryo.get();
            return kryo.readObject(input, cls);
        }
    }
}
