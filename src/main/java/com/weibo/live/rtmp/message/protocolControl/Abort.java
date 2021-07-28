package com.weibo.live.rtmp.message.protocolControl;

import com.weibo.live.rtmp.constants.MessageType;
import com.weibo.live.rtmp.message.RtmpPayload;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName Abort
 * @Description 中断（2）
 * @Author hebiao1
 * @Date 2021/6/25 12:11 下午
 * @Version 1.0
 */
@Data
@NoArgsConstructor
public class Abort extends RtmpProtocolControlMessage {
    int chunkAStreamId;

    public Abort(int chunkAStreamId) {
        this.chunkAStreamId = chunkAStreamId;
    }

    @Override
    public ByteBuf encode() {
        return Unpooled.buffer(4).writeInt(chunkAStreamId);
    }

    @Override
    public RtmpPayload decode(ByteBuf payload) {
        this.chunkAStreamId = payload.readInt();
        return this;
    }

    @Override
    public int getMessageType() {
        return MessageType.ABORT;
    }
}
