package com.weibo.live.rtmp.message;

import com.weibo.live.rtmp.constants.MessageType;
import com.weibo.live.rtmp.message.dataMessage.RtmpDataMessage;
import com.weibo.live.rtmp.message.commandMessage.RtmpCommandMessage;
import com.weibo.live.rtmp.message.media.AudioMediaMessage;
import com.weibo.live.rtmp.message.media.VideoMediaMessage;
import com.weibo.live.rtmp.message.messageHeader.RtmpHeader;
import com.weibo.live.rtmp.message.protocolControl.*;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName RtmpMessage
 * @Description TODO
 * @Author hebiao1
 * @Date 2021/6/24 6:31 下午
 * @Version 1.0
 */
@Slf4j
@Data
public class RtmpMessage {
    private final RtmpHeader rtmpHeader;
    private RtmpPayload rtmpPayload;

    public RtmpMessage(RtmpHeader rtmpHeader) {
        this.rtmpHeader = rtmpHeader;
    }

    public void decodePayload(ByteBuf payload) {
        switch (rtmpHeader.getMessageHeader().getMessageType()) {
            case MessageType.SET_CHUNK_SIZE: {
                this.rtmpPayload = new SetChunkSize().decode(payload);
            }
            break;
            case MessageType.ABORT: {
                this.rtmpPayload = new Abort().decode(payload);
            }
            break;
            case MessageType.ACKNOWLEDGEMENT: {
                this.rtmpPayload = new Acknowledgement().decode(payload);
            }
            break;
            case MessageType.SET_PEER_BANDWIDTH: {
                this.rtmpPayload = new SetPeerBandwidth().decode(payload);
            }
            break;
            case MessageType.WINDOW_ACKNOWLEDGEMENT_SIZE: {
                this.rtmpPayload = new WindowAcknowledgementSize().decode(payload);
            }
            break;
            case MessageType.COMMAND_AMF0: {
                this.rtmpPayload = new RtmpCommandMessage().decode(payload);
            }
            break;
            case MessageType.DATA_MESSAGE_AMF0: {
                this.rtmpPayload = new RtmpDataMessage().decode(payload);
            }
            break;
            case MessageType.AUDIO: {
                this.rtmpPayload = new AudioMediaMessage().decode(payload);
            }
            break;
            case MessageType.VIDEO: {
                this.rtmpPayload = new VideoMediaMessage().decode(payload);
            }
            break;
            default:
                log.info("message rtmpHeader={},payload={}", rtmpHeader, payload);
                break;
        }


    }


}
