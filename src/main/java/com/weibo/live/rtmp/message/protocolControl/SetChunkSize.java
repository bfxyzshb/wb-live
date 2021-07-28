package com.weibo.live.rtmp.message.protocolControl;


import com.weibo.live.rtmp.constants.MessageType;
import com.weibo.live.rtmp.message.RtmpPayload;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ChunkSize
 * @Description 设置chunk 大小（1）
 * @Author hebiao1
 * @Date 2021/6/25 12:10 下午
 * @Version 1.0
 */
@Data
@NoArgsConstructor

public class SetChunkSize extends RtmpProtocolControlMessage {
    private int chunkSize;

    @Override
    public ByteBuf encode() {
        return Unpooled.buffer(4).writeInt(chunkSize);

    }

    public RtmpPayload decode(ByteBuf payload) {
        this.chunkSize = payload.readInt();
        return this;
    }

    @Override
    public int getMessageType() {
        return MessageType.SET_CHUNK_SIZE;
    }

    private final static AttributeKey<Integer> CHUNK_SIZE = AttributeKey.valueOf("CHUNK_SIZE");

    public static void setChunkSize(Channel channel, int chunkSize) {
        channel.attr(CHUNK_SIZE).set(chunkSize);
    }

    public static int getChunkSize(Channel channel) {
        return channel.attr(CHUNK_SIZE).get() == null ? 128 : channel.attr(CHUNK_SIZE).get();
    }
}
