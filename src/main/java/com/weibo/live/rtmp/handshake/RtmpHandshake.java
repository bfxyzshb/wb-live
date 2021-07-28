package com.weibo.live.rtmp.handshake;

import com.weibo.live.rtmp.constants.Handshake;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @ClassName RtmpHandshake
 * @Description TODO
 * @Author hebiao1
 * @Date 2021/6/24 5:07 下午
 * @Version 1.0
 */

/**
 * --------------
 * c->s c0+c1
 * s-> s0+s1+s2
 * c-> c2
 * --------------
 */
@Slf4j
public class RtmpHandshake extends ByteToMessageDecoder {

    Boolean c0c1Done = true;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int readableLength = in.readableBytes();
        //先读c0+c1 大小1537 byte
        if (c0c1Done) {
            //读取1537个byte  ByteBuf默认的缓存大小为1024
            if (readableLength < Handshake.VERSION_LENGTH + Handshake.RANDOMBYTE_LENGTH) {
                log.info("read version error c0c1Length={}", readableLength);
                //少于1537继续读取
                return;
            }
            in.readBytes(new byte[Handshake.VERSION_LENGTH + Handshake.RANDOMBYTE_LENGTH]);
            //写s0+s1+s2
            writeS0S1S2(ctx);
            c0c1Done = false;
        } else {
            //读c2
            if (readableLength < Handshake.RANDOMBYTE_LENGTH) {
                log.info("read c2 error c2Length={}", readableLength);
                return;
            }
            in.readBytes(Handshake.RANDOMBYTE_LENGTH);
            ctx.channel().pipeline().remove(this);
        }
    }

    private void writeS0S1S2(ChannelHandlerContext ctx) {
        ByteBuf byteBuf = Unpooled.buffer(Handshake.VERSION_LENGTH + Handshake.RANDOMBYTE_LENGTH + Handshake.RANDOMBYTE_LENGTH);
        //s0 version
        byteBuf.writeByte(Handshake.VERSION);
        //s1 time
        byteBuf.writeBytes(new byte[Handshake.TIME]);
        //s1 zore 4字节
        byteBuf.writeInt(0);
        //s1 random byte 1528
        byteBuf.writeBytes(new byte[Handshake.RANDOM_CONTENT_LENGTH]);
        //s2 time
        byteBuf.writeBytes(new byte[Handshake.TIME]);
        //s2 time2
        byteBuf.writeBytes(new byte[Handshake.TIME2]);
        //s2 random byte 1528
        byteBuf.writeBytes(new byte[Handshake.RANDOM_CONTENT_LENGTH]);
        ctx.writeAndFlush(byteBuf);
    }

}
