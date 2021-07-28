package com.weibo.live.rtmp.message.messageHeader;

import lombok.Data;

/**
 * @ClassName ExtTimeStamp
 * @Description TODO
 * @Author hebiao1
 * @Date 2021/6/25 10:10 上午
 * @Version 1.0
 */
@Data
public class ExtTimeStamp extends RtmpMessageHeader {
    private int extTimeStamp;
}
