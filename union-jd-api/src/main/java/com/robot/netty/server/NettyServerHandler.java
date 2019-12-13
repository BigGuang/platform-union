package com.robot.netty.server;

import com.waps.utils.StringUtils;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 处理调度类
 */

@Component
@Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {

    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Autowired(required = false)
    @Qualifier("closeFutureHandler")
    public Handler closeFutureHandler;
    @Autowired(required = false)
    @Qualifier("exceptionFutureHandler")
    public Handler exceptionFutureHandler;
    @Autowired
    @Qualifier("bussinessFutureHandler")
    public Handler bussinessFutureHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
//		System.out.println(((HandlerServiceImp) exportServiceMap.get("helloWorldService")).test());
//		// 返回客户端消息 - 我已经接收到了你的消息

        String uuid = ctx.channel().id().asLongText();

        String retMsg = bussinessFutureHandler.hander(uuid, msg);
        if (!StringUtils.isNull(retMsg)) {
            ctx.writeAndFlush(retMsg);//写回消息
        }

        //一下代码正式时应关闭
        //返回数据给客户端,是一个异步的操作
//        System.out.println("当前在线:" + channelGroup.size());
//        channelGroup.forEach(channel -> {
//            System.out.println("channel:" + channel);
//            if (ctx.channel() != channel) {
//                //给其他客户端显示的内容
//                channel.writeAndFlush(retMsg + "\n");
//            } else {
//                //给自己客户端看到的消息
////                channel.writeAndFlush("是自己的消息\n");
//            }
//        });
//        channelGroup.writeAndFlush("服务器已经收到你们客户端消息了！");

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        channelGroup.writeAndFlush("broadcast：" + ctx.channel().remoteAddress() + " 注册进了服务器\n");
        channelGroup.add(ctx.channel());
        super.handlerAdded(ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " channelRegistered ");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " channelUnregistered ");
        super.channelUnregistered(ctx);
        if (closeFutureHandler != null) {
            closeFutureHandler.hander(ctx.name());
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        //添加身边标识
//        ctx.channel().attr(null);
        channelGroup.writeAndFlush(ctx.channel().remoteAddress() + "上线了\n");
        System.out.println(ctx.channel().remoteAddress() + " channelActive ");

        //上线后添加到在线列表
        String uuid = ctx.channel().id().asLongText();
        OnLineService.addSocketChannel(uuid, (SocketChannel) ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channelGroup.writeAndFlush(ctx.channel().remoteAddress() + "下线了\n");
        System.out.println(ctx.channel().remoteAddress() + " channelInactive ");

        //下线后从列表中删除
        String uuid = ctx.channel().id().asLongText();
        OnLineService.removeSocketChannel(uuid);
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " exceptionCaught :" + cause.getMessage());
        super.exceptionCaught(ctx, cause);
        if (exceptionFutureHandler != null) {
            exceptionFutureHandler.hander(cause.getMessage());
        }
    }
}
