package com.weibo.live.rtmp.message.protocolControl;

import com.weibo.live.rtmp.constants.MessageType;
import com.weibo.live.rtmp.message.RtmpPayload;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName Acknowledgement
 * @Description 应答窗口大小（3）
 * @Author hebiao1
 * @Date 2021/6/25 12:11 下午
 * @Version 1.0
 */
@Data
@NoArgsConstructor

public class Acknowledgement extends RtmpProtocolControlMessage {
    int sequenceNumber;

    public Acknowledgement(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    @Override
    public ByteBuf encode() {
        return Unpooled.buffer(4).writeInt(sequenceNumber);
    }

    @Override
    public RtmpPayload decode(ByteBuf payload) {
        this.sequenceNumber = payload.readInt();
        return this;
    }

    @Override
    public int getMessageType() {
        return MessageType.ACKNOWLEDGEMENT;
    }
}
