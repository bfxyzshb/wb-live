package com.weibo.live.rtmp.message.media.eunm;

import java.util.Arrays;

/**
 * @ClassName NaluUnitType
 * @Description TODO
 * @Author hebiao1
 * @Date 2021/7/8 11:59 上午
 * @Version 1.0
 * nal_unit_type
 * NAL 单元和 RBSP 语法结构的内容
 *
 *
 * 0	未指定
 * 1	一个非IDR图像的编码条带
 * slice_layer_without_partitioning_rbsp( )
 * 2	编码条带数据分割块A
 * slice_data_partition_a_layer_rbsp( )
 * 3	编码条带数据分割块B
 * slice_data_partition_b_layer_rbsp( )
 * 4	编码条带数据分割块C
 * slice_data_partition_c_layer_rbsp( )
 * 5	IDR图像的编码条带
 * slice_layer_without_partitioning_rbsp( )
 * 6	辅助增强信息 (SEI)
 * sei_rbsp( )
 * 7	序列参数集
 * seq_parameter_set_rbsp( )
 * 8	图像参数集
 * pic_parameter_set_rbsp( )
 * 9	访问单元分隔符
 * access_unit_delimiter_rbsp( )
 * 10	序列结尾
 * end_of_seq_rbsp( )
 * 11	流结尾
 * end_of_stream_rbsp( )
 * 12	填充数据
 * filler_data_rbsp( )
 * 13	序列参数集扩展
 * seq_parameter_set_extension_rbsp( )
 * 14...18	保留
 * 19	未分割的辅助编码图像的编码条带
 * slice_layer_without_partitioning_rbsp( )
 * 20...23	保留
 * 24...31	未指定
 */
public enum  NaluUnitType {
    SLICE_LAYER_WITHOUT_PARTITIONING_RBSP(1, "一个非IDR图像的编码条带"),
    SLICE_DATA_PARTITION_A_LAYER_RBSP(2, "编码条带数据分割块A"),
    SLICE_DATA_PARTITION_B_LAYER_RBSP(3, "编码条带数据分割块B"),
    SLICE_DATA_PARTITION_C_LAYER_RBSP(4, "编码条带数据分割块C"),
    IDR(5, "IDR"),
    SEI(6, "sei"),
    SPS(7, "sps"),
    PPS(8, "pps"),
    ACCESS_UNIT_DELIMITER_RBSP(9,"访问单元分隔符"),
    END_OF_SEQ_RBSP(10,"序列结尾"),
    END_OF_STREAM_RBSP(11,"流结尾"),
    FILLER_DATA_RBSP(12,"填充数据"),
    SEQ_PARAMETER_SET_EXTENSION_RBSP(13,"序列参数集扩展"),
    UNKNOWN(0, "unknown");
    int type;
    String desc;

    NaluUnitType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static NaluUnitType parseOf(int value) {
        return Arrays.stream(NaluUnitType.values()).filter(t -> t.type == value)
                .findFirst().orElse(UNKNOWN);
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
