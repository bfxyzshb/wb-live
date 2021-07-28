package com.weibo.live.rtmp.message.protocolControl;

import com.weibo.live.rtmp.constants.MessageType;
import com.weibo.live.rtmp.message.RtmpPayload;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName SetPeerBandwidth
 * @Description 设置带宽（6）
 * @Author hebiao1
 * @Date 2021/6/25 12:21 下午
 * @Version 1.0
 */
@Data
@NoArgsConstructor

public class SetPeerBandwidth extends RtmpProtocolControlMessage {
    int acknowledgeWindowSize;
    byte limitType;

    public SetPeerBandwidth(int acknowledgeWindowSize, byte limitType) {
        this.acknowledgeWindowSize = acknowledgeWindowSize;
        this.limitType = limitType;
    }

    @Override
    public ByteBuf encode() {
        return Unpooled.buffer(5).writeInt(acknowledgeWindowSize).writeByte(limitType);

    }

    @Override
    public RtmpPayload decode(ByteBuf payload) {
        this.acknowledgeWindowSize = payload.readInt();
        this.limitType = payload.readByte();
        return this;
    }

    @Override
    public int getMessageType() {
        return MessageType.SET_PEER_BANDWIDTH;
    }
}
