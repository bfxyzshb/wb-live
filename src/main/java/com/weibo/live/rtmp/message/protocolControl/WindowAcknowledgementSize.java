package com.weibo.live.rtmp.message.protocolControl;

import com.weibo.live.rtmp.constants.MessageType;
import com.weibo.live.rtmp.message.RtmpPayload;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName WindowAcknowledgementSize
 * @Description 应答窗口大小（5）
 * @Author hebiao1
 * @Date 2021/6/25 12:11 下午
 * @Version 1.0
 */
@Data
@NoArgsConstructor
public class WindowAcknowledgementSize extends RtmpProtocolControlMessage {
    int acknowledgeWindowSize;

    public WindowAcknowledgementSize(int acknowledgeWindowSize) {
        this.acknowledgeWindowSize = acknowledgeWindowSize;
    }

    @Override
    public ByteBuf encode() {
        return Unpooled.buffer(4).writeInt(acknowledgeWindowSize);
    }

    @Override
    public RtmpPayload decode(ByteBuf payload) {
        this.acknowledgeWindowSize =payload.readInt();
        return this;
    }

    @Override
    public int getMessageType() {
        return MessageType.WINDOW_ACKNOWLEDGEMENT_SIZE;
    }

}
