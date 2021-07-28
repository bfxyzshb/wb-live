package com.weibo.live.rtmp.encode;

import com.weibo.live.rtmp.constants.FmtType;
import com.weibo.live.rtmp.message.RtmpPayload;
import com.weibo.live.rtmp.message.media.MediaMessage;
import com.weibo.live.rtmp.message.messageHeader.BasicHeader;
import com.weibo.live.rtmp.message.messageHeader.ExtTimeStamp;
import com.weibo.live.rtmp.message.messageHeader.MessageHeader;
import com.weibo.live.rtmp.message.protocolControl.SetChunkSize;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName MessageEncoder
 * @Description TODO
 * @Author hebiao1
 * @Date 2021/7/1 11:37 上午
 * @Version 1.0
 */
@Slf4j
public class RtmpMessageEncoder extends MessageToByteEncoder<RtmpPayload> {
    private static long beginTimeStamp = System.currentTimeMillis();
    private Map<Integer, MessageHeader> prevMessageHeader = new HashMap<>();


    @Override
    protected void encode(ChannelHandlerContext ctx, RtmpPayload msg, ByteBuf out) throws Exception {
        if (msg instanceof MediaMessage) {
            return;
        }

        ByteBuf byteBuf = Unpooled.buffer();
        //要发送的payload
        ByteBuf payload = msg.encode();
        MessageHeader prevMsgHeader = prevMessageHeader.get(msg.getCsid());
        while (payload.isReadable()) {
            int messageLength = Math.min(payload.readableBytes(), SetChunkSize.getChunkSize(ctx.channel()));
            chunkMessage(msg, byteBuf, payload, messageLength, prevMsgHeader);
        }

        out.writeBytes(byteBuf);
    }

    private void chunkMessage(RtmpPayload msg, ByteBuf byteBuf, ByteBuf payload, int messageLength, MessageHeader prevMsgHeader) {
        if (prevMsgHeader == null) {
            packageChunk(msg, byteBuf, payload.readBytes(messageLength), FmtType.CHUNK_FMT_TYPE_0);
        } else if(prevMsgHeader.getMessageLength() != messageLength){
            packageChunk(msg, byteBuf, payload.readBytes(messageLength), FmtType.CHUNK_FMT_TYPE_1);
        }  else if (prevMsgHeader.getMessageLength() == messageLength && prevMsgHeader.getMessageType() == msg.getMessageType()) {
            packageChunk(msg, byteBuf, payload.readBytes(messageLength), FmtType.CHUNK_FMT_TYPE_3);
        }
    }

    /**
     * 组装chunk
     *
     * @param msg
     * @param byteBuf
     * @param payload
     * @param fmt
     */
    private void packageChunk(RtmpPayload msg, ByteBuf byteBuf, ByteBuf payload, Integer fmt) {
        //1.basic header
        BasicHeader basicHeader = new BasicHeader();
        basicHeader.setCsid(msg.getCsid());
        basicHeader.setFmt(fmt);
        basicHeader.encodeFmtAndCsid(byteBuf);
        //2.message header
        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setTimestamp(0);
        messageHeader.setMessageLength(payload.readableBytes());
        messageHeader.setTimestampDelta(0);
        messageHeader.setMessageType(msg.getMessageType());
        messageHeader.setMessageStreamId(msg.getMessageStreamId());
        //3.extTimeStamp
        ExtTimeStamp extTimeStamp = new ExtTimeStamp();
        extTimeStamp.setExtTimeStamp(0);
        messageHeader.encodeMessageHeader(byteBuf, basicHeader, extTimeStamp);
        log.info("response basicHeader={} messageHeader={}",basicHeader,messageHeader);
        //4.chunkData
        byteBuf.writeBytes(payload);
        prevMessageHeader.put(msg.getCsid(), messageHeader);
    }
}
