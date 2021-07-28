package com.weibo.live.rtmp.message;

import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * @ClassName RtmpPayload
 * @Description TODO
 * @Author hebiao1
 * @Date 2021/6/25 4:02 下午
 * @Version 1.0
 */
@Data
public abstract class RtmpPayload {
    public final int messageStreamId = 0;

    public abstract ByteBuf encode();

    public abstract RtmpPayload decode(ByteBuf payload);

    public abstract int getCsid();

    public abstract int getMessageType();

    public int getMessageStreamId() {
        return messageStreamId;
    }
}
