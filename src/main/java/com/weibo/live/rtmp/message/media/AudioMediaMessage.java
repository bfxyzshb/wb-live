package com.weibo.live.rtmp.message.media;

import com.weibo.live.rtmp.constants.MessageType;
import com.weibo.live.rtmp.message.RtmpPayload;
import io.netty.buffer.ByteBuf;

/**
 * @ClassName MediaMessage
 * @Description 8
 * @Author hebiao1
 * @Date 2021/6/25 5:42 下午
 * @Version 1.0
 */
public class AudioMediaMessage extends MediaMessage {

    @Override
    public int getCsid() {
        return 100;
    }

    @Override
    public int getMessageType() {
        return MessageType.AUDIO;
    }

    @Override
    public int getMessageStreamId() {
        return super.getMessageStreamId();
    }
}
