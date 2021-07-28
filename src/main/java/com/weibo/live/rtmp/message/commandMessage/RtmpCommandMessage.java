package com.weibo.live.rtmp.message.commandMessage;

import com.google.common.collect.Lists;
import com.weibo.live.rtmp.amf.*;
import com.weibo.live.rtmp.amf.messaging.io.ASObject;
import com.weibo.live.rtmp.constants.MessageType;
import com.weibo.live.rtmp.message.RtmpPayload;
import com.weibo.live.rtmp.message.protocolControl.SetChunkSize;
import com.weibo.live.rtmp.message.protocolControl.SetPeerBandwidth;
import com.weibo.live.rtmp.message.protocolControl.WindowAcknowledgementSize;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName RtmpCommandMessage
 * @Description TODO
 * @Author hebiao1
 * @Date 2021/6/25 4:53 下午
 * @Version 1.0
 */
@Data
@Slf4j
@NoArgsConstructor
public class RtmpCommandMessage extends RtmpPayload {
    List<Object> command = new ArrayList<>();


    public RtmpCommandMessage(List<Object> command) {
        this.command = command;
    }

    @Override
    public ByteBuf encode() {
        ByteBuf buffer = Unpooled.buffer();
        AMF0Serializer amf0Serializer = new AMF0Serializer(buffer);
        amf0Serializer.encodePayload(buffer, this.command);
        return buffer;
    }

    @Override
    public RtmpPayload decode(ByteBuf payload) {
        AMF0Deserializer amf0Deserializer = new AMF0Deserializer(payload);
        this.command = amf0Deserializer.decodeAmfPayload();
        return this;
    }

    @Override
    public int getCsid() {
        return 3;
    }

    @Override
    public int getMessageType() {
        return MessageType.COMMAND_AMF0;
    }


    public void handleCommand(ChannelHandlerContext ctx) {
        List<Object> command = this.getCommand();
        String commandName = (String) command.get(0);
        log.info("commandName={}", commandName);
        switch (commandName) {
            case "connect":
                handleConnect(ctx);
                break;
            case "createStream":
                handleCreateStream(ctx);
                break;
            case "FCPublish":
                handlePublish(ctx);
                break;
            case "play":
                break;
            case "deleteStream":
            case "closeStream":
                break;
            default:
                break;
        }
    }

    private void handlePublish(ChannelHandlerContext ctx) {
        // reply a onStatus
        RtmpCommandMessage onStatus = onStatus("status", "NetStream.Publish.Start", "Start publishing");
        ctx.writeAndFlush(onStatus);
    }


    private void handleCreateStream(ChannelHandlerContext ctx) {
        List<Object> result = new ArrayList<Object>();
        result.add("_result");
        result.add(this.getCommand().get(1));// transaction id
        result.add(null);// properties
        result.add(5);// stream id

        RtmpCommandMessage response = new RtmpCommandMessage(result);

        ctx.writeAndFlush(response);

    }

    /**
     * +--------------+                              +-------------+
     * |    Client    |             |                |    Server   |
     * +------+-------+             |                +------+------+
     * |              Handshaking done               |
     * |                     |                       |
     * |                     |                       |
     * |                     |                       |
     * |                     |                       |
     * |----------- Command Message(connect) ------->|
     * |                                             |
     * |<------- Window Acknowledgement Size --------|
     * |                                             |
     * |<----------- Set Peer Bandwidth -------------|
     * |                                             |
     * |-------- Window Acknowledgement Size ------->|
     * |                                             |
     * |<------ User Control Message(StreamBegin) ---|
     * |                                             |
     * |<------------ Command Message ---------------|
     * |       (_result- connect response)           |
     * |                                             |
     *
     * @param ctx
     */
    private void handleConnect(ChannelHandlerContext ctx) {

        String app = (String) ((Map) this.getCommand().get(2)).get("app");
        Integer clientRequestEncode = (Integer) ((Map) this.getCommand().get(2)).get("objectEncoding");
        if (clientRequestEncode != null && clientRequestEncode.intValue() == 3) {
            log.error("client={} request AMF3 encoding but server currently doesn't support", ctx);
            ctx.close();
            return;
        }
        int acknowledgeWindowSize = 5000000;
        WindowAcknowledgementSize windowAcknowledgementSize = new WindowAcknowledgementSize(acknowledgeWindowSize);
        ctx.writeAndFlush(windowAcknowledgementSize);
        SetPeerBandwidth setPeerBandwidth = new SetPeerBandwidth(acknowledgeWindowSize, MessageType.SET_PEER_BANDWIDTH_TYPE_SOFT);
        ctx.writeAndFlush(setPeerBandwidth);
        SetChunkSize setChunkSize=new SetChunkSize();
        setChunkSize.setChunkSize(60000);
        ctx.writeAndFlush(setChunkSize);
        List<Object> result = Lists.newArrayList();
        result.add("_result");
        result.add(this.getCommand().get(1));// transaction id
        ASObject asObject = new ASObject("map");
        asObject.put("fmsVer", "FMS/3,0,1,123");
        asObject.put("capabilities", 31);
        asObject.put("level", "status");
        asObject.put("code", "NetConnection.Connect.Success");
        asObject.put("description", "Connection succeeded");
        asObject.put("author", "shaohebiao");
        asObject.put("version", "wb-live 1.0");
        asObject.put("objectEncoding", 0);
        result.add(asObject);
        RtmpCommandMessage response = new RtmpCommandMessage(result);
        ctx.writeAndFlush(response);

    }

    public RtmpCommandMessage onStatus(String level, String code, String description) {
        List<Object> result = new ArrayList<>();
        result.add("onStatus");
        result.add(0);// always 0
        result.add(null);// properties
        ASObject asObject = new ASObject();
        asObject.put("level", level);
        asObject.put("code", code);
        asObject.put("description", description);
        result.add(asObject);
        RtmpCommandMessage response = new RtmpCommandMessage(result);
        return response;
    }
}
