package edgecloud.devicemanager.apiserver;


import edgecloud.devicemanager.DeviceManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

        private Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);

        @Override
        public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
            if(msg.getDecoderResult() != DecoderResult.SUCCESS) {
                ctx.close();
                return;
            }

            String method = msg.getMethod().toString();
            if (!method.equals("POST")) {
                return;
            }
            ByteBuf content = msg.content();
            String param = content.toString(Charset.forName("UTF-8"));

            JSONObject jo = new JSONObject(param);

            String eventName =  jo.get("eventName").toString();
            String input = jo.get("Event").toString();
            logger.info("event name " + eventName);
            logger.info(DeviceManager.eventFunctionMap.toString());
            String funName = DeviceManager.eventFunctionMap.get(eventName);
            logger.info("funName  " + funName);
            jo.append("funcList", funName);
            String result = DeviceManager.sendMessageToClient(2, jo.toString());
            jo.append("result", result);

            // 响应JSON
//            String responseJson = String.format("{\"funcName\": \"%s\"}", funName);
            String responseJson = jo.toString();
            byte[] responseBytes = responseJson.getBytes("UTF-8");
            int contentLength = responseBytes.length;

            // 构造FullHttpResponse对象，FullHttpResponse包含message body
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(responseBytes));
            response.headers().set("Content-Type", "application/json;charset=UTF-8");
            response.headers().set("Content-Length", Integer.toString(contentLength));

            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }


}
