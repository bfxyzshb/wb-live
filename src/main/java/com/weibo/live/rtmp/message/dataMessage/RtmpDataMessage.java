package com.weibo.live.rtmp.message.dataMessage;

import com.weibo.live.rtmp.amf.AMF0Deserializer;
import com.weibo.live.rtmp.amf.AMF0Serializer;
import com.weibo.live.rtmp.constants.MessageType;
import com.weibo.live.rtmp.message.RtmpPayload;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName RtmpDataMessage
 * @Description TODO
 * @Author hebiao1
 * @Date 2021/6/25 5:38 下午
 * @Version 1.0
 */
@Data
@Slf4j
public class RtmpDataMessage extends RtmpPayload {
    List<Object> data = new ArrayList<>();

    @Override
    public ByteBuf encode() {
        ByteBuf buffer = Unpooled.buffer();
        AMF0Serializer amf0Serializer = new AMF0Serializer(buffer);
        amf0Serializer.encodePayload(buffer, data);
        return buffer;
    }

    @Override
    public RtmpPayload decode(ByteBuf payload) {
        AMF0Deserializer amf0Deserializer = new AMF0Deserializer(payload);
        this.data = amf0Deserializer.decodeAmfPayload();
        log.info("metaData={}", data);
        return this;
    }

    @Override
    public int getCsid() {
        return 104;
    }

    @Override
    public int getMessageType() {
        return MessageType.DATA_MESSAGE_AMF0;
    }
}
