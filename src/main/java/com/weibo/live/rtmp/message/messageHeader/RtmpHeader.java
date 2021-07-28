package com.weibo.live.rtmp.message.messageHeader;

import lombok.Data;

/**
 * @ClassName RtmpHeader
 * @Description TODO
 * @Author hebiao1
 * @Date 2021/6/25 10:05 上午
 * @Version 1.0
 */
@Data
public class RtmpHeader {
    private BasicHeader basicHeader;
    private MessageHeader messageHeader;
    private ExtTimeStamp extTimeStamp;

    public int getMessageHeaderLength() {
        return basicHeader.getMessageHeaderLength() + messageHeader.getMessageHeaderLength() + extTimeStamp.getMessageHeaderLength();
    }

    @Override
    public String toString() {
        return "RtmpHeader{" +
                "basicHeader=" + basicHeader +
                ", messageHeader=" + messageHeader +
                ", extTimeStamp=" + extTimeStamp +
                ", headerLength=" + getMessageHeaderLength() +
                '}';
    }
}
