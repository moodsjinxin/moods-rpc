package moods.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import moods.serialization.Serializer;
import moods.transport.netty.constant.Constant;

import java.util.List;


public class rpcDecode extends ByteToMessageDecoder {
    private Class<?> myclass;
    private Serializer serializer;

    public rpcDecode(Class<?> myclass,Serializer serializer){
        this.myclass = myclass;
        this.serializer = serializer;
    }


    /**
     * ChannelHandlerContext创建于ChannelHandler被载入到ChannelPipeline的时候
     * ChannelHandlerContext主要功能是管理在同一ChannelPipeline中各个ChannelHandler的交互
     * */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < Constant.LENGTH_FIELD_LENGTH) {
            return;
        }

        // 加书签，想要返回该标记的时候用resetReaderIndex
        in.markReaderIndex();

        //读取请求的长度
        int len = in.readInt();

        // 如果数据不够头部的长度
        if (in.readableBytes() < len) {
            in.resetReaderIndex();
            return;
        }

        byte[] data = new byte[len];
        in.readBytes(data);

        // 把二进制反序列化
        Object obj = serializer.deserialize(data, myclass);

        /**
         * NETTY 用引用计数来管理回收
         * retain 引用计数加1
         * release 引用计数减1
         * */
        ReferenceCountUtil.retain(obj);
        out.add(obj);
    }
}
