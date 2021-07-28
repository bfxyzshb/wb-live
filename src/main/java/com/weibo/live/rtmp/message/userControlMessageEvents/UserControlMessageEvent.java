package com.weibo.live.rtmp.message.userControlMessageEvents;

import com.weibo.live.rtmp.constants.MessageType;
import com.weibo.live.rtmp.message.RtmpPayload;
import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * @ClassName UserControlMessageEvent
 * @Description TODO
 * @Author hebiao1
 * @Date 2021/6/27 7:24 下午
 * @Version 1.0
 */
@Data
public class UserControlMessageEvent extends RtmpPayload {
    short eventType;
    int data;

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
        return 105;
    }

    @Override
    public int getMessageType() {
        return MessageType.USER_CONTROL_MESSAGE_EVENTS;
    }
}
