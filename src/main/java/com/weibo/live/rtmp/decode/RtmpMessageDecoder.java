package com.weibo.live.rtmp.decode;

import com.google.common.collect.Maps;
import com.weibo.live.rtmp.message.RtmpMessage;

import com.weibo.live.rtmp.message.messageHeader.BasicHeader;
import com.weibo.live.rtmp.message.messageHeader.ExtTimeStamp;
import com.weibo.live.rtmp.message.messageHeader.MessageHeader;
import com.weibo.live.rtmp.message.messageHeader.RtmpHeader;
import com.weibo.live.rtmp.message.protocolControl.SetChunkSize;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @ClassName Decoder
 * @Description 粘包 拆包处理
 * @Author hebiao1
 * @Date 2021/6/24 5:07 下午
 * @Version 1.0
 */
@Slf4j
public class RtmpMessageDecoder extends ReplayingDecoder<RtmpMessageState> {

    public RtmpMessageDecoder() {
        super(RtmpMessageState.HEADER);
    }

    //缓存chunkHeader
    Map<Integer, RtmpHeader> cacheChunkHeader = Maps.newHashMap();
    //缓存payload
    Map<Integer, ByteBuf> cacheChunkPayload = Maps.newHashMap();
    ByteBuf cacheChunkData = null;
    int currentCsid;
    RtmpHeader rtmpHeader = null;



    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() <= 0) {
            return;
        }
        RtmpMessageState rtmpMessageState = state();
        //header
        if (rtmpMessageState == RtmpMessageState.HEADER) {
            rtmpHeader = new RtmpHeader();
            BasicHeader basicHeader = new BasicHeader();
            basicHeader.decodeFmtAndCsid(in);
            rtmpHeader.setBasicHeader(basicHeader);
            currentCsid = basicHeader.getCsid();
            MessageHeader messageHeader = new MessageHeader();
            RtmpHeader prevRtmpHeader = cacheChunkHeader.get(basicHeader.getCsid());
            ExtTimeStamp extTimeStamp = new ExtTimeStamp();
            messageHeader.decodeMessageHeader(basicHeader, prevRtmpHeader == null ? null : prevRtmpHeader.getMessageHeader(), extTimeStamp, in);
            rtmpHeader.setMessageHeader(messageHeader);
            rtmpHeader.setExtTimeStamp(extTimeStamp);
            //log.info("rtmpHeader={}", rtmpHeader);
            cacheChunkHeader.put(basicHeader.getCsid(), rtmpHeader);
            //接受payload cache,由于message会分多个chunk
            cacheChunkData = cacheChunkPayload.get(basicHeader.getCsid());
            if (cacheChunkData == null) {
                cacheChunkData = Unpooled.buffer(messageHeader.getMessageLength());
                cacheChunkPayload.put(basicHeader.getCsid(), cacheChunkData);
            }
            checkpoint(RtmpMessageState.PAYLOAD);
        }
        if (rtmpMessageState == RtmpMessageState.PAYLOAD) {
            //可读字节数
            int writableBytes = Math.min(cacheChunkData.writableBytes(), SetChunkSize.getChunkSize(ctx.channel()));
            byte[] bytes = new byte[writableBytes];
            in.readBytes(bytes);
            cacheChunkData.writeBytes(bytes);
            checkpoint(RtmpMessageState.HEADER);
            //缓存chunk 可写继续执行
            if (cacheChunkData.isWritable()) {
                return;
            }
            //解析payload
            ByteBuf payload = cacheChunkPayload.get(currentCsid);
            cacheChunkPayload.remove(currentCsid);
            RtmpMessage rtmpMessage = new RtmpMessage(rtmpHeader);
            rtmpMessage.decodePayload(payload);
            out.add(rtmpMessage);
        }

    }


}
