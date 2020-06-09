package moods.transport.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import moods.common.RPCResponse;
import moods.filter.filterMannager;

import java.net.InetSocketAddress;

public class rpcResponsHandler extends SimpleChannelInboundHandler<RPCResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RPCResponse response) throws Exception {
        //过滤后
        if(filterMannager.afterFilter != null){
            InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            filterMannager.afterFilter.invoke(response,socketAddress.getAddress()+":"+socketAddress.getPort());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //log.warn("RPC request exception: {}", cause);
        System.out.println("RPC request exception: "+cause);
    }
}
