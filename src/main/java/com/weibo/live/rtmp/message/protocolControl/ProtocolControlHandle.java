package com.weibo.live.rtmp.message.protocolControl;

import com.weibo.live.rtmp.message.RtmpMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName protocolControlHandle
 * @Author hebiao1
 * @Date 2021/6/27 6:36 下午
 * @Version 1.0
 */
@Slf4j
public class ProtocolControlHandle extends SimpleChannelInboundHandler<RtmpMessage> {
    int receiveByteCount = 0;
    int winAcknowledgementSize = 0;
    int historyReceiveByteCount = 0;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RtmpMessage msg) throws Exception {
        if (msg.getRtmpPayload() instanceof SetChunkSize) {
            SetChunkSize setChunkSize = (SetChunkSize) msg.getRtmpPayload();
            SetChunkSize.setChunkSize(ctx.channel(), setChunkSize.getChunkSize());
            log.info("set chunk size={}", setChunkSize.getChunkSize());
        }
        //接收指定数据大小给客户端返回ack
        if (msg.getRtmpPayload() instanceof WindowAcknowledgementSize) {
            WindowAcknowledgementSize windowAcknowledgementSize = (WindowAcknowledgementSize) msg.getRtmpPayload();
            winAcknowledgementSize = windowAcknowledgementSize.getAcknowledgeWindowSize();
        }
        send2ClientWinAcknowledgement(ctx, msg);
        if (!(msg.getRtmpPayload() instanceof RtmpProtocolControlMessage)) {
            ctx.fireChannelRead(msg);
        }
    }

    private void send2ClientWinAcknowledgement(ChannelHandlerContext ctx, RtmpMessage msg) {
        int receiveBytes = msg.getRtmpHeader().getMessageHeaderLength() + msg.getRtmpHeader().getMessageHeader().getMessageLength();
        receiveByteCount += receiveBytes;
        historyReceiveByteCount += receiveBytes;
        if (winAcknowledgementSize <= 0) {
            return;
        }
        if (receiveByteCount >= winAcknowledgementSize) {
            receiveByteCount = 0;
            log.info("ProtocolControlHandle historyReceiveByteCount={}", historyReceiveByteCount);
            ctx.writeAndFlush(new Acknowledgement(historyReceiveByteCount));
        }
    }
}
