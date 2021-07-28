package com.weibo.live.rtmp.message.media.eunm;

import java.util.Arrays;

public enum CodeId {
    JPEG(1, "jpeg"),
    sorensonH263(2, "sorenson H.263"),
    screenVideo(3, "screen video"),
    on2VP6(4, "on2 VP6"),
    on2VP6WithalphaChannel(5, "on2 VP6 with alpha channel"),
    screenVideoVersion(6, "screen video version 2"),
    AVC(7, "avc h264"),
    UNKNOWN(0, "unknown");

    int type;
    String desc;

    CodeId(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static CodeId parseOf(int value) {
        return Arrays.stream(CodeId.values()).filter(t -> t.type == value)
                .findFirst().orElse(UNKNOWN);
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
