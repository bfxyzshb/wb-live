package com.weibo.live.rtmp.message.media.eunm;

import java.util.Arrays;

/**
 * @ClassName AVCPacketType
 * @Description TODO
 * @Author hebiao1
 * @Date 2021/7/2 6:13 下午
 * @Version 1.0
 */
public enum AVCPacketType {
    AVCSEQUENCEHEADER(0, "avc sequence header"),

    AVCNALU(1, "avc nalu"),

    AVCENDOFSEQUENCE(2, "avc end of sequence"),

    UNKNOWN(-1, "unknown");

    int type;
    String desc;

    AVCPacketType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static AVCPacketType parseOf(int value) {
        return Arrays.stream(AVCPacketType.values()).filter(t -> t.type == value)
                .findFirst().orElse(UNKNOWN);
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
