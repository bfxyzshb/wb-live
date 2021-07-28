package com.weibo.live;

import com.weibo.live.rtmp.decode.RtmpMessageDecoder;
import com.weibo.live.rtmp.encode.RtmpMessageEncoder;
import com.weibo.live.rtmp.message.protocolControl.ProtocolControlHandle;
import com.weibo.live.rtmp.handle.RtmpMessageHandle;
import com.weibo.live.rtmp.handshake.RtmpHandshake;
import com.weibo.live.websocket.NioWebSocketServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @ClassName LiveServer
 * @Author hebiao1
 * @Date 2021/6/24 5:03 下午
 * @Version 1.0
 */
public class LiveServer {

    public static void main(String[] args) throws Exception {

        // 1. 首先创建 两个线程组 BossGroup和WorkerGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup(1);

        // 2. 创建服务端启动类，配置启动参数
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        // 3. 配置具体的参数，配置具体参数
        /**
         * 3.1 配置group
         * 3.2 使用 NioServerSocketChannel 作为服务器的通道实现
         * 3.3 设置具体的Handler
         */
        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //4. 给 pipeline 添加处理器，每当有连接accept时，就会运行到此处。
                        ChannelPipeline pipeline = socketChannel.pipeline()/*.addLast(new NettyServerHandler())*/;
                        pipeline.addLast(new RtmpHandshake());
                        pipeline.addLast(new RtmpMessageDecoder());
                        pipeline.addLast(new RtmpMessageEncoder());
                        pipeline.addLast(new ProtocolControlHandle());
                        pipeline.addLast(new RtmpMessageHandle());
                    }
                }).option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
        // 5. 绑定端口并且同步，生成了一个ChannelFuture 对象
        ChannelFuture channelFuture = serverBootstrap.bind(1935).sync();


        NioWebSocketServer nioWebSocketServer = new NioWebSocketServer();
        nioWebSocketServer.init();


        System.out.println("server is ready……");




    }
}
