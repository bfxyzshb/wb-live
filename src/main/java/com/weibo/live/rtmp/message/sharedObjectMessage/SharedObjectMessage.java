package com.weibo.live.rtmp.message.sharedObjectMessage;

import com.weibo.live.rtmp.constants.MessageType;
import com.weibo.live.rtmp.message.RtmpPayload;
import io.netty.buffer.ByteBuf;

/**
 * @ClassName SharedObjectMessage
 * @Description TODO
 * @Author hebiao1
 * @Date 2021/6/25 5:44 下午
 * @Version 1.0
 */
public class SharedObjectMessage extends RtmpPayload {


    @Override
    public ByteBuf encode() {
        return null;
    }

    @Override
    public RtmpPayload decode(ByteBuf payload) {
        return null;
    }

    @Override
    public int getCsid() {
        return 102;
    }

    @Override
    public int getMessageType() {
        return MessageType.SHARED_OBJECT_MESSAGE_AMF0;
    }
}
