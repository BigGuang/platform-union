<%--
  Created by IntelliJ IDEA.
  User: xguang
  Date: 2018/3/5
  Time: 上午10:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<html>
<head>
    <title>400</title>
</head>
<body>
<div>
    <H1>错误：</H1><%=exception%>
</div>
<div>
    <H2>错误内容：</H2>
    <%
        if(exception!=null && response!=null) {
            exception.printStackTrace(response.getWriter());
        }
    %>
</div>

<div>
    <%if (request.getAttribute("javax.servlet.error.status_code") != null) {%>
    错误码： <%=request.getAttribute("javax.servlet.error.status_code")%>
    <%}%>
</div>
<div>
    <%if (request.getAttribute("javax.servlet.error.message") != null) {%>
    信息： <%=request.getAttribute("javax.servlet.error.message")%>
    <%}%>
</div>
<div>
    <%if (request.getAttribute("javax.servlet.error.exception_type") != null) {%>
    异常： <%=request.getAttribute("javax.servlet.error.exception_type")%>
    <%}%>
</div>
</body>
</html>
