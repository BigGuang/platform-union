package com.robot.netty.server;

public interface Handler {
    public static String COMMONRET="200";
    public String hander(String msg);
    public String hander(String uuid,String msg);
}
