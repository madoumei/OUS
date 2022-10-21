package com.config.netty;

import com.client.controller.EquipmentController;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;


@Component
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private StringRedisTemplate strRedisTemplate;

    private static NettyServerHandler nettyServerHandler;

    @PostConstruct
    public void init() {
        nettyServerHandler = this;
    }

    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            ByteBuf buf = (ByteBuf) msg;
            String recieved = convertByteBufToString(buf);
            String uuid = recieved.substring(recieved.indexOf("&") + 1);
            EquipmentController.updateHeartbeat(uuid);
            ValueOperations<String, String> valueOperations = nettyServerHandler.strRedisTemplate.opsForValue();
            if (null != valueOperations.get("uuid_" + uuid)) {
                nettyServerHandler.strRedisTemplate.delete("uuid_" + uuid);
                ctx.writeAndFlush(getSendByteBuf("uploadLog"));
                System.out.println(recieved);
            } else {
                ctx.writeAndFlush(getSendByteBuf("200"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//		if (!Util.isEmptyString(recieved)) {
//			try {
//				ctx.writeAndFlush(getSendByteBuf("APPLE"));
//			
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
//		}
        /*
         * try { ctx.writeAndFlush(getSendByteBuf("APPLE")); } catch
         * (UnsupportedEncodingException e) { e.printStackTrace(); }
         */
    }

    public String bytesToHexFun2(byte[] bytes) {
        char[] buf = new char[bytes.length * 2];
        int index = 0;
        for (byte b : bytes) { // 利用位运算进行转换，可以看作方法一的变种
            buf[index++] = HEX_CHAR[b >>> 4 & 0xf];
            buf[index++] = HEX_CHAR[b & 0xf];
        }

        return new String(buf);
    }

    public String convertByteBufToString(ByteBuf buf) {
        String str;
        if (buf.hasArray()) { // 处理堆缓冲区
            str = new String(buf.array(), buf.arrayOffset() + buf.readerIndex(), buf.readableBytes());
        } else { // 处理直接缓冲区以及复合缓冲区
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            str = new String(bytes, 0, buf.readableBytes());
        }
        return str;
    }

    /*
     * 从ByteBuf中获取信息 使用UTF-8编码返回
     */
    private String getMessage(ByteBuf buf) {
        byte[] con = new byte[buf.readableBytes()];
        buf.readBytes(con);

        try {
            return bytesToHexFun2(con);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ByteBuf getSendByteBuf(String message) throws UnsupportedEncodingException {

        byte[] req = message.getBytes("UTF-8");
        ByteBuf pingMessage = Unpooled.buffer();
        pingMessage.writeBytes(req);

        return pingMessage;
    }
}
