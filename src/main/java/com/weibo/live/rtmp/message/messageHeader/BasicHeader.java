package com.weibo.live.rtmp.message.messageHeader;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName BasicHeader
 * @Author hebiao1
 * @Date 2021/6/25 10:05 上午
 * @Version 1.0
 */
@Data
@Slf4j
public class BasicHeader extends RtmpMessageHeader {
    private int fmt;
    private int csid;

    public void decodeFmtAndCsid(ByteBuf in) {
        byte firstByte = in.readByte();
        this.messageHeaderLength += 1;
        //fmt
        int fmt = (firstByte & 0xff) >> 6;
        //csid
        int csid = (firstByte & 0x37);
        if (csid == 0) {
            csid = (firstByte & 0xff) + 64;
            this.messageHeaderLength += 1;
        } else if (csid == 1) {
            byte secondByte = in.readByte();
            byte thirdByte = in.readByte();
            csid = (thirdByte & 0xff) << 8 + (secondByte & 0xff) + 64;
            this.messageHeaderLength += 2;
        } else if (csid >= 2) {
            //do nothing
        }
        this.fmt = fmt;
        this.csid = csid;
    }

    public void encodeFmtAndCsid(ByteBuf byteBuf) {
        byte[] bytes = null;
        if (csid <= 63) {
            bytes = new byte[]{(byte) ((fmt << 6) + csid)};
        } else if (csid <= 320) {
            bytes = new byte[]{(byte) (fmt << 6), (byte) (csid - 64)};
        } else {
            bytes = new byte[]{(byte) ((fmt << 6) | 1), (byte) ((csid - 64) & 0xff), (byte) ((csid - 64) >> 8)};
        }
        byteBuf.writeBytes(bytes);
    }


}
