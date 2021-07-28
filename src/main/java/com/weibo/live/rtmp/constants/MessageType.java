package com.weibo.live.rtmp.constants;

/**
 * @ClassName MessageType
 * @Description 消息类型
 * @Author hebiao1
 * @Date 2021/6/25 3:35 下午
 * @Version 1.0
 */
public class MessageType {
    // 协议控制消息
    public final static int SET_CHUNK_SIZE = 1;
    public final static int ABORT = 2;
    public final static int ACKNOWLEDGEMENT = 3;
    public final static int WINDOW_ACKNOWLEDGEMENT_SIZE = 5;
    public final static int SET_PEER_BANDWIDTH = 6;

    //command 消息
    public final static int COMMAND_AMF0 = 20;
    public final static int COMMAND_AMF3 = 17;

    // Data Message
    public final static int DATA_MESSAGE_AMF0 = 18;
    public final static int DATA_MESSAGE_AMF3 = 15;

    //mediate Message
    public final static int AUDIO = 8;
    public final static int VIDEO = 9;

    // Shared Object Message
    public static final int SHARED_OBJECT_MESSAGE_AMF0 = 19;
    public static final int SHARED_OBJECT_MESSAGE_AMF3 = 16;

    public final static int AGGREGATE_MESSAGE = 22;
    public final static byte SET_PEER_BANDWIDTH_TYPE_SOFT = 2;

    // User Control Message Events
    public static final int USER_CONTROL_MESSAGE_EVENTS = 4;


}
