package com.robot.netty.server.handlerImpl;

import com.robot.netty.server.Handler;

public class ExceptionFutureHandler  implements Handler {
    private Handler nextHandler;

    public Handler getNextHandler() {
        return nextHandler;
    }

    public void setNextHandler(Handler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public String hander(String msg) {
        System.out.println("出现异常，异常信息：" + msg);
        if (nextHandler != null) {
            nextHandler.hander(msg);
        }

        return COMMONRET;
    }

    @Override
    public String hander(String uuid, String msg) {
        System.out.println("出现异常，异常信息：" + msg);
        if (nextHandler != null) {
            nextHandler.hander(msg);
        }

        return COMMONRET;
    }
}
