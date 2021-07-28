package com.weibo.live.rtmp.message.messageHeader;

import com.weibo.live.rtmp.constants.FmtType;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import static com.weibo.live.rtmp.constants.FmtType.MAX_TIMESTAMP;

/**
 * @ClassName MessageHeader
 * @Author hebiao1
 * @Date 2021/6/25 10:05 上午
 * @Version 1.0
 */
@Data
@Slf4j
public class MessageHeader extends RtmpMessageHeader {
    int timestamp;
    int messageLength;
    int messageType;
    int messageStreamId;
    int timestampDelta;


    public void decodeMessageHeader(BasicHeader basicHeader, MessageHeader prevMessageHeader, ExtTimeStamp extTimeStamp, ByteBuf in) {
        if (basicHeader == null) {
            log.info("decodeMessageHeader basicHeader is null");
        }
        switch (basicHeader.getFmt()) {
            case FmtType.CHUNK_FMT_TYPE_0: {
                //1.timestamp 3个字节
                int timestamp = in.readMedium();
                this.setTimestamp(timestamp);
                //2.message length 3个字节
                int messageLength = in.readMedium();
                this.setMessageLength(messageLength);
                //3.message type id 1个字节
                int messageType = in.readByte();
                this.setMessageType(messageType);
                //4.message stream id 4个字节
                int messageStreamId = in.readIntLE();
                this.setMessageStreamId(messageStreamId);
                this.messageHeaderLength += 11;
                if (timestamp == MAX_TIMESTAMP) {
                    int extendedTimestamp = in.readInt();
                    extTimeStamp.setExtTimeStamp(extendedTimestamp);
                    extTimeStamp.messageHeaderLength += 4;
                }
            }
            break;
            case FmtType.CHUNK_FMT_TYPE_1: {
                //1.timestamp delta 3个字节
                int timeStampDelta = in.readMedium();
                this.setTimestampDelta(timeStampDelta);
                //2.message length 3个字节
                int messageLength = in.readMedium();
                this.setMessageLength(messageLength);
                //3.message type id 1个字节
                int messageType = in.readByte();
                this.setMessageType(messageType);
                //4 message stream id 和上一个chunk 一样
                this.setMessageStreamId(prevMessageHeader.getMessageStreamId());
                this.messageHeaderLength += 7;
                if (timeStampDelta == MAX_TIMESTAMP) {
                    int extendedTimestamp = in.readInt();
                    extTimeStamp.setExtTimeStamp(extendedTimestamp);
                    extTimeStamp.messageHeaderLength += 4;
                }
            }
            break;
            case FmtType.CHUNK_FMT_TYPE_2: {
                //1.timestamp delta 3个字节
                int timeStampDelta = in.readMedium();
                //其余和上一个chunk一样
                this.setTimestampDelta(timeStampDelta);
                this.setMessageStreamId(prevMessageHeader.getMessageStreamId());
                this.setMessageType(prevMessageHeader.getMessageType());
                this.setMessageLength(prevMessageHeader.getMessageLength());
                this.messageHeaderLength += 3;
                if (timeStampDelta == MAX_TIMESTAMP) {
                    int extendedTimestamp = in.readInt();
                    extTimeStamp.setExtTimeStamp(extendedTimestamp);
                    extTimeStamp.messageHeaderLength += 4;
                }
            }
            break;
            case FmtType.CHUNK_FMT_TYPE_3: {
                this.setMessageLength(prevMessageHeader.getMessageLength());
                this.setMessageType(prevMessageHeader.getMessageType());
                this.setMessageStreamId(prevMessageHeader.getMessageStreamId());
                this.setTimestampDelta(prevMessageHeader.getTimestampDelta());
                this.setTimestamp(prevMessageHeader.getTimestamp());
            }
            break;
            default: {
                throw new RuntimeException("illegal fmt type:" + basicHeader.getFmt());
            }
        }
    }


    public void encodeMessageHeader(ByteBuf byteBuf, BasicHeader basicHeader, ExtTimeStamp extTimeStamp) {
        Integer fmt = basicHeader.getFmt();
        if (fmt == FmtType.CHUNK_FMT_TYPE_0) {
            this.timestamp = 0;
            byteBuf.writeMedium(this.timestamp);
            byteBuf.writeMedium(this.messageLength);
            byteBuf.writeByte(this.messageType);
            byteBuf.writeIntLE(this.messageStreamId);
            if (this.timestamp == MAX_TIMESTAMP) {
                byteBuf.writeInt(extTimeStamp.getExtTimeStamp());
            }
        } else if (fmt == FmtType.CHUNK_FMT_TYPE_1) {
            byteBuf.writeMedium(this.timestampDelta);
            byteBuf.writeMedium(this.messageLength);
            byteBuf.writeByte(this.messageType);
            if (this.timestampDelta == MAX_TIMESTAMP) {
                byteBuf.writeInt(extTimeStamp.getExtTimeStamp());
            }
        } else if (fmt == FmtType.CHUNK_FMT_TYPE_2) {
            byteBuf.writeInt(this.timestampDelta);
            if (this.timestampDelta == MAX_TIMESTAMP) {
                byteBuf.writeMedium(extTimeStamp.getExtTimeStamp());
            }
        } else if (fmt == FmtType.CHUNK_FMT_TYPE_3) {
            //do nothing
            return;
        }
    }
}
