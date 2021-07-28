package com.weibo.live.rtmp.message.protocolControl;

import com.weibo.live.rtmp.message.RtmpPayload;

/**
 * @ClassName RtmpProtocolontrolMessage
 * rtmp 协议控制消息
 * 协议控制消息必须使用 0 作为消息流ID（作为已知的控制流ID），同时使用 2 作为块流ID
 * @Author hebiao1
 * @Date 2021/6/25 12:03 下午
 * @Version 1.0
 */

public abstract class RtmpProtocolControlMessage extends RtmpPayload {

    private int csid = 2;

    private int messageStreamId = 0;


    public int getCsid() {
        return csid;
    }

    public int getMessageStreamId() {
        return messageStreamId;
    }
}
