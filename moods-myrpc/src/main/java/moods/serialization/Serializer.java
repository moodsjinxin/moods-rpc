package moods.serialization;

import java.io.IOException;

public interface Serializer {
    //序列化
    <T> byte[] serialize(T obj) throws IOException;

    //反序列化
    <T> T deserialize(byte[] data, Class<T> cls) throws IOException;

}
