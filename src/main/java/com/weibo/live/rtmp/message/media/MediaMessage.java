package com.weibo.live.rtmp.message.media;

import com.weibo.live.rtmp.message.RtmpPayload;
import io.netty.buffer.ByteBuf;

/**
 * @ClassName MediaMessage
 * @Description TODO
 * @Author hebiao1
 * @Date 2021/7/1 3:52 下午
 * @Version 1.0
 */
public abstract class MediaMessage extends RtmpPayload {
    ByteBuf data;


    @Override
    public ByteBuf encode() {
        return data;
    }

    @Override
    public RtmpPayload decode(ByteBuf payload) {
        this.data = payload;
        return this;
    }

    @Override
    public int getMessageStreamId() {
        return super.getMessageStreamId();
    }
}
