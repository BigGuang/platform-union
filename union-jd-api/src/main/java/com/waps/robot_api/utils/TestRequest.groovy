package com.waps.robot_api.utils

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class TestRequest {
    public static void outPrintRequest(HttpServletRequest request) {
        StringBuffer buff = new StringBuffer();
        buff.append("ip:" + request.getRemoteHost() + "\r\n");
        Enumeration param_names = request.getParameterNames();
        buff.append("======Parameter======\r\n");
        while (param_names.hasMoreElements()) {
            String pname = (String) param_names.nextElement();
            String value = request.getParameter(pname);
            if (value == null) value = "";
            buff.append(pname + ":" + value + "\r\n");
        }
        Enumeration header_names = request.getHeaderNames();
        buff.append("======header======\r\n");
        while (header_names.hasMoreElements()) {
            String header = (String) header_names.nextElement();
            String value = request.getHeader(header);
            if (value == null) value = "";
            buff.append(header + ":" + value + "\r\n");
        }
//    buff.append("======Attribute======\r\n");
//    Enumeration attr_names = request.getAttributeNames();
//    while (attr_names.hasMoreElements()) {
//        String attr = (String) attr_names.nextElement();
//        String value = (String) request.getAttribute(attr);
//        if (value == null) value = "";
//        buff.append(attr + ":" + value + "\r\n");
//    }
        buff.append("======Cookie======\r\n");
        Cookie[] mycookies = request.getCookies();
        if (mycookies != null) {
            for (int i = 0; i < mycookies.length; i++) {
                buff.append(mycookies[i].getName() + ":" + mycookies[i].getValue() + "\r\n");
            }
        }
        buff.append("=======request.getRequestedSessionId()=======\r\n");
        buff.append(request.getRequestedSessionId() + "\r\n");
        buff.append("============request============\r\n");
        buff.append("getServerName=" + request.getServerName() + "\r\n");
        buff.append("getServletPath=" + request.getServletPath() + "\r\n");
        buff.append("getContextPath=" + request.getContextPath() + "\r\n");
        buff.append("getAuthType=" + request.getAuthType() + "\r\n");
        buff.append("getRemoteUser=" + request.getRemoteUser() + "\r\n");
        buff.append("getRequestURI=" + request.getRequestURI() + "\r\n");
        buff.append("getRequestURL=" + request.getRequestURL() + "\r\n");
        buff.append("getPathInfo=" + request.getPathInfo() + "\r\n");
        buff.append("getPathTranslated=" + request.getPathTranslated() + "\r\n");
        buff.append("getScheme=" + request.getScheme() + "\r\n");
        buff.append("getLocalAddr=" + request.getLocalAddr() + "\r\n");
        buff.append("getCharacterEncoding=" + request.getCharacterEncoding() + "\r\n");
        buff.append("getLocalName=" + request.getLocalName() + "\r\n");
        System.out.println(buff.toString());
    }
}
