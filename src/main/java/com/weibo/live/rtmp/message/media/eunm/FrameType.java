package com.weibo.live.rtmp.message.media.eunm;


import java.util.Arrays;
public enum FrameType {
    keyFrame(1, "key-frame"),
    interFrame(2, "inter-frame"),
    disposableFrame(3, "disposable-frame"),
    generatedFrame(4, "generated-frame"),
    vodieInfoOrCommandFrame(5, "vodieInfo/command-frame"),
    UNKNOWN(0, "unknown");
    int type;
    String desc;

    FrameType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static FrameType parseOf(int value) {
        return Arrays.stream(FrameType.values()).filter(t -> t.type == value)
                .findFirst().orElse(UNKNOWN);
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
