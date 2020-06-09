package moods.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import moods.serialization.Serializer;

public class rpcCode extends MessageToByteEncoder {
    private Class<?> myclass;
    private Serializer serializer;

    public rpcCode(Class<?> myclass, Serializer serializer){
        this.myclass = myclass;
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf out) throws Exception {
        if(myclass.isInstance(msg)){
            byte[] data = serializer.serialize(msg);
            // 先设置数据长度
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}
