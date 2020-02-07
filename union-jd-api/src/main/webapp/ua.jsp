<%@ page import="java.util.Enumeration" %><%--
  Created by IntelliJ IDEA.
  User: xguang
  Date: 2019/10/18
  Time: 1:31 下午
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<%
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
    buff.append("============response============\r\n");
    buff.append("getCharacterEncoding=" + response.getCharacterEncoding() + "\r\n");
    buff.append("getContentType=" + response.getContentType() + "\r\n");
    buff.append("getBufferSize=" + response.getBufferSize() + "\r\n");
    System.out.println(buff.toString());
    out.println(buff.toString().replaceAll("\r\n","</br>"));

%>

</body>
</html>
