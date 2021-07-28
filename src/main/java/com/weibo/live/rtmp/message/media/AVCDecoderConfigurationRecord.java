package com.weibo.live.rtmp.message.media;

import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * @ClassName AVCDecoderConfigurationRecord
 * @Description TODO
 * @Author hebiao1
 * @Date 2021/7/2 3:00 下午
 * @Version 1.0
 */
@Data
public class AVCDecoderConfigurationRecord {
    private ByteBuf byteBuf;
    int configurationVersion;
    int AVCProfileIndication;
    int profileCompatibility;
    int AVCLevelIndication;
    int reserved1;
    int lengthSizeMinusOne;
    int reserved2;
    int numOfSequenceParameterSets;
    int sequenceParameterSetLength;
    //
    int numOfPictureParameterSets;
    int pictureParameterSetLength;
    //


    public AVCDecoderConfigurationRecord(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    //8bit
    @Order(value = 1)
    public AVCDecoderConfigurationRecord parseConfigurationVersion() {
        this.configurationVersion = (byteBuf.readByte() & 0xff);
        return this;
    }

    //8bit
    @Order(value = 2)
    public AVCDecoderConfigurationRecord parseAVCProfileIndication() {
        this.AVCProfileIndication = (byteBuf.readByte() & 0xff);
        return this;
    }

    //8bit
    @Order(value = 3)
    public AVCDecoderConfigurationRecord parseProfileCompatibility() {
        this.profileCompatibility = (byteBuf.readByte() & 0xff);
        return this;
    }

    //8bit
    @Order(value = 4)
    public AVCDecoderConfigurationRecord parseAVCLevelIndication() {
        this.AVCLevelIndication = (byteBuf.readByte() & 0xff);
        return this;
    }

    //6bit 2bit
    @Order(value = 5)
    public AVCDecoderConfigurationRecord parseReserved1AndLengthSizeMinusOne() {
        byte b = byteBuf.readByte();
        this.reserved1 = ((b >> 2) & 0xff);
        this.lengthSizeMinusOne = b & 0x3;
        return this;
    }

    //3 & 5 bit
    @Order(value = 6)
    public AVCDecoderConfigurationRecord parseReserved2AndnumOfSequenceParameterSets() {
        byte b = byteBuf.readByte();
        this.reserved2 = ((b >> 5) & 0xff);
        this.numOfSequenceParameterSets = b & 0x1f;
        return this;
    }

    //16 bit
    @Order(value = 7)
    public AVCDecoderConfigurationRecord parseSequenceParameterSetLength() {
        this.sequenceParameterSetLength = byteBuf.readShort();
        byte[] bytes = new byte[this.sequenceParameterSetLength];
        byteBuf.readBytes(bytes);
        return this;
    }

    //8bit pps个数
    @Order(value = 8)
    public AVCDecoderConfigurationRecord parseNumOfPictureParameterSets() {
        this.numOfPictureParameterSets = (byteBuf.readByte() & 0xff);
        return this;
    }

    //16 bit
    @Order(value = 9)
    public AVCDecoderConfigurationRecord parsepictureParameterSetLength() {
        this.pictureParameterSetLength = byteBuf.readShort();
        byte[] bytes = new byte[this.pictureParameterSetLength];
        byteBuf.readBytes(bytes);
        return this;
    }

    @Override
    public String toString() {
        return "AVCDecoderConfigurationRecord{" +
                ", configurationVersion=" + configurationVersion +
                ", AVCProfileIndication=" + AVCProfileIndication +
                ", profileCompatibility=" + profileCompatibility +
                ", AVCLevelIndication=" + AVCLevelIndication +
                ", reserved=" + reserved1 +
                ", lengthSizeMinusOne=" + lengthSizeMinusOne +
                ", reserved=" + reserved2 +
                ", numOfSequenceParameterSets=" + numOfSequenceParameterSets +
                ", sequenceParameterSetLength=" + sequenceParameterSetLength +
                ", numOfPictureParameterSets=" + numOfPictureParameterSets +
                ", pictureParameterSetLength=" + pictureParameterSetLength +
                '}';
    }
}
