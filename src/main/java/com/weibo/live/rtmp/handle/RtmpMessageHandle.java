package com.weibo.live.rtmp.handle;

import com.weibo.live.rtmp.message.RtmpMessage;
import com.weibo.live.rtmp.message.RtmpPayload;
import com.weibo.live.rtmp.message.commandMessage.RtmpCommandMessage;
import com.weibo.live.rtmp.message.dataMessage.RtmpDataMessage;
import com.weibo.live.rtmp.message.media.AudioMediaMessage;
import com.weibo.live.rtmp.message.media.VideoMediaMessage;
import com.weibo.live.rtmp.message.userControlMessageEvents.UserControlMessageEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @ClassName RtmpMessageHandle
 * @Description TODO
 * @Author hebiao1
 * @Date 2021/6/25 7:58 下午
 * @Version 1.0
 */
@Slf4j
public class RtmpMessageHandle extends SimpleChannelInboundHandler<RtmpMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RtmpMessage msg) throws Exception {
        RtmpPayload rtmpPayload = msg.getRtmpPayload();
        if (rtmpPayload instanceof RtmpCommandMessage) {
            ((RtmpCommandMessage) rtmpPayload).handleCommand(ctx);
        } else if (rtmpPayload instanceof RtmpDataMessage) {
            handleDataMessage(ctx, (RtmpDataMessage) rtmpPayload);
        } else if (rtmpPayload instanceof AudioMediaMessage) {
            //handleMedia(ctx, rtmpPayload);
        } else if (rtmpPayload instanceof VideoMediaMessage) {
            handleMedia(ctx, (VideoMediaMessage) rtmpPayload);
        } else if (rtmpPayload instanceof UserControlMessageEvent) {
            handleUserControl(ctx, (UserControlMessageEvent) rtmpPayload);
        }
    }

    private void handleUserControl(ChannelHandlerContext ctx, UserControlMessageEvent msg) {
//		boolean isBufferLength = msg.isBufferLength();
//		if (isBufferLength) {
//			if (role == Role.Subscriber) {
//				startPlay(ctx, streamManager.getStream(streamName));
//			}
//		}

    }

    private void handleMedia(ChannelHandlerContext ctx, VideoMediaMessage videoMediaMessage) {
        videoMediaMessage.keyFrame();
    }

    private void handleDataMessage(ChannelHandlerContext ctx, RtmpDataMessage msg) {

        String name = (String) msg.getData().get(0);
        if ("@setDataFrame".equals(name)) {
            // save on metadata
            Map<String, Object> properties = (Map<String, Object>) msg.getData().get(2);
            properties.remove("filesize");

        }

    }


}
