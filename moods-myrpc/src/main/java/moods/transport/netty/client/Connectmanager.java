package moods.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;


import io.netty.channel.socket.nio.NioSocketChannel;
import moods.serialization.Serializer;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Connectmanager {
    private Serializer serializer;

    private Connectmanager(Serializer serializer){
        this.serializer = serializer;
    }

    private Map<InetSocketAddress, Channel> addressChannelMap = new ConcurrentHashMap<InetSocketAddress, Channel>();

    public Channel getChannel(InetSocketAddress inetSocketAddress){
        if(addressChannelMap.containsKey(inetSocketAddress)){
            return addressChannelMap.get(inetSocketAddress);
        }

        Channel channel = null;
        //netty的调度模块
        EventLoopGroup group =new NioEventLoopGroup();
        try {
            // netty的客户端开发
            Bootstrap bootstrap =   new Bootstrap();
            //配置客户端参数
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new rpcChannelInitializer(serializer))
                    .option(ChannelOption.SO_KEEPALIVE,true);


            //与服务端连接
            channel =  bootstrap.connect(inetSocketAddress.getHostName(),inetSocketAddress.getPort()).sync().channel();

            System.out.println("连接到"+inetSocketAddress.toString());

            //存放连接
            addChannel(inetSocketAddress,  channel);

            //如果channel关闭，移除channel
            channel.closeFuture().addListener((ChannelFutureListener) future -> removeChannel(inetSocketAddress));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return channel;
    }

    private void addChannel(InetSocketAddress inetSocketAddress,Channel channel) {
        addressChannelMap.put(inetSocketAddress,  channel);
    }

    private void removeChannel(InetSocketAddress inetSocketAddress) {
        addressChannelMap.remove(inetSocketAddress);
    }
}
