package moods.transport.netty.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import moods.coder.rpcCode;
import moods.coder.rpcDecode;
import moods.common.RPCRequest;
import moods.common.RPCResponse;
import moods.serialization.Serializer;

public class rpcChannelInitializer extends ChannelInitializer<SocketChannel> {

    private Serializer serializer;

    public rpcChannelInitializer(Serializer serializer){
        this.serializer = serializer;
    }
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline cp = socketChannel.pipeline();
        cp.addLast(new rpcCode(RPCRequest.class, serializer));
        cp.addLast(new rpcDecode(RPCResponse.class, serializer));
        cp.addLast(new rpcResponsHandler());
    }
}
