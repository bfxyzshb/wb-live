package com.weibo.live.rtmp.constants;

/**
 * @ClassName Handshake
 * @Description TODO
 * @Author hebiao1
 * @Date 2021/6/24 5:41 下午
 * @Version 1.0
 */
public class Handshake {
    //版本号
    public static byte VERSION = 3;

    public static int RANDOMBYTE_LENGTH = 1536;
    public static int VERSION_LENGTH = 1;
    public static int TIME = 4;
    public static int TIME2 = 4;
    public static byte ZERO = 0;
    public static int ZERO_LENGTH = 4;
    public static int RANDOM_CONTENT_LENGTH = 1528;

}
