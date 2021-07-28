package com.weibo.live.rtmp.message.media;

import com.weibo.live.rtmp.message.media.eunm.FrameType;
import com.weibo.live.rtmp.message.media.eunm.NaluUnitType;
import com.weibo.live.websocket.NioWebSocketHandler;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName Nalu
 * @Description TODO
 * @Author hebiao1
 * @Date 2021/7/8 11:38 上午
 * @Version 1.0
 */
@Slf4j
public class Nalu {

    private final ByteBuf byteBuf;
    byte startCode1 = 0x00000001;
    byte startCode2 = 0x000001;

    public Nalu(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    //avcc
    public void avccNalu() {
        while (byteBuf.isReadable()) {
            int naluLength = byteBuf.readInt();
            byte[] b = new byte[naluLength];
            byteBuf.readBytes(b);
            log.info("=========" + NaluUnitType.parseOf(b[0] & 0x1f).getDesc());
            NioWebSocketHandler nioWebSocketHandler = new NioWebSocketHandler();
            nioWebSocketHandler.handlerWebSocketFrame(NaluUnitType.parseOf(b[0] & 0x1f).getDesc());
        }
    }

    //annex-B
    public void annexBNalu() {

    }


}
