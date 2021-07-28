package com.weibo.live.rtmp.constants;

import java.nio.charset.Charset;

/**
 * @ClassName AMF0type
 * @Description TODO
 * @Author hebiao1
 * @Date 2021/6/30 2:35 下午
 * @Version 1.0
 */
public class AMF0Type {
    /**
     * UTF-8 is used
     */
    public static final Charset CHARSET = Charset.forName("UTF-8");

    /**
     * Max string lenght constant
     */
    public static final int LONG_STRING_LENGTH = 65535;

    /**
     * Number marker constant
     */
    public static final byte TYPE_NUMBER = 0x00;

    /**
     * Boolean value marker constant
     */
    public static final byte TYPE_BOOLEAN = 0x01;

    /**
     * String marker constant
     */
    public static final byte TYPE_STRING = 0x02;

    /**
     * Object marker constant
     */
    public static final byte TYPE_OBJECT = 0x03;

    /**
     * MovieClip marker constant
     */
    public static final byte TYPE_MOVIECLIP = 0x04;

    /**
     * Null marker constant
     */
    public static final byte TYPE_NULL = 0x05;

    /**
     * Undefined marker constant
     */
    public static final byte TYPE_UNDEFINED = 0x06;

    /**
     * Object reference marker constant
     */
    public static final byte TYPE_REFERENCE = 0x07;

    /**
     * Mixed array marker constant
     */
    public static final byte TYPE_MIXED_ARRAY = 0x08;

    /**
     * End of object marker constant
     */
    public static final byte TYPE_END_OF_OBJECT = 0x09;

    /**
     * Array marker constant
     */
    public static final byte TYPE_ARRAY = 0x0A;

    /**
     * Date marker constant
     */
    public static final byte TYPE_DATE = 0x0B;

    /**
     * Long string marker constant
     */
    public static final byte TYPE_LONG_STRING = 0x0C;

    /**
     * Unsupported type marker constant
     */
    public static final byte TYPE_UNSUPPORTED = 0x0D;

    /**
     * Recordset marker constant
     */
    public static final byte TYPE_RECORDSET = 0x0E;

    /**
     * XML marker constant
     */
    public static final byte TYPE_XML = 0x0F;

    /**
     * Class marker constant
     */
    public static final byte TYPE_CLASS_OBJECT = 0x10;

    /**
     * Object marker constant (for AMF3)
     */
    public static final byte TYPE_AMF3_OBJECT = 0x11;

    /**
     * true marker constant
     */
    public static final byte VALUE_TRUE = 0x01;

    /**
     * false marker constant
     */
    public static final byte VALUE_FALSE = 0x00;
}
