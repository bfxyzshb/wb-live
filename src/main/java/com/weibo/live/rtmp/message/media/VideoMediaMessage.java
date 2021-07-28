package com.weibo.live.rtmp.message.media;

import com.weibo.live.rtmp.constants.MessageType;
import com.weibo.live.rtmp.message.media.eunm.AVCPacketType;
import com.weibo.live.rtmp.message.media.eunm.CodeId;
import com.weibo.live.rtmp.message.media.eunm.FrameType;
import com.weibo.live.websocket.NioWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

/**
 * @ClassName videoMediaMessage
 * @Description 9
 * @Author hebiao1
 * @Date 2021/6/25 5:43 下午
 * @Version 1.0
 */
@Slf4j
public class VideoMediaMessage extends MediaMessage {
    @Override
    public int getCsid() {
        return 103;
    }

    @Override
    public int getMessageType() {
        return MessageType.VIDEO;
    }

    public void parseAVCDecoderConfigurationRecord() {

    }

    public void keyFrame() {
        //读一个字节
        byte firstByte = this.data.readByte();
        if (firstByte == 0x12) {
            log.info("is keyFrame");
        }
        int frameType = (firstByte >> 4 & 0xf);
        int codeId = (firstByte & 0x0f);
        log.info(FrameType.parseOf(frameType).getDesc());
        log.info(CodeId.parseOf(codeId).getDesc());

        //读一个字节
        byte avcPacketTypeByte = this.data.readByte();
        int avcPacketType = avcPacketTypeByte & 0xff;
        //log.info(AVCPacketType.parseOf(avcPacketType).getDesc());

        //CompositionTime 跳过三个字节
        this.data.readMedium();

        //avc sequence header
        if (AVCPacketType.AVCSEQUENCEHEADER.equals(AVCPacketType.parseOf(avcPacketType))) {
            AVCDecoderConfigurationRecord avcDecoderConfigurationRecord = new AVCDecoderConfigurationRecord(this.data);
            OrderHandler.orderHandle(avcDecoderConfigurationRecord, AVCDecoderConfigurationRecord.class);
            //log.info(avcDecoderConfigurationRecord.toString());
        } else if (AVCPacketType.AVCNALU.equals(AVCPacketType.parseOf(avcPacketType))) {
            Nalu nalu = new Nalu(this.data);
            nalu.avccNalu();
        }

    }

    public static String byteToBit(byte b) {
        return "" + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)

                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)

                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)

                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);

    }


}
