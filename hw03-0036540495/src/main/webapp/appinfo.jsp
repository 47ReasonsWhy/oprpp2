<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%
    long currentTime = System.currentTimeMillis();
    long appStartTime = (long)application.getAttribute("appStartTime");
    long appRunningTime = currentTime - appStartTime;
    long seconds = appRunningTime / 1000;
    long minutes = seconds / 60;
    long hours = minutes / 60;
    long days = hours / 24;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>App info</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/getcolor">
</head>
<body>
    <h1>App info</h1>
    <p>This web app is running for <%=days%> days, <%=hours%> hours, <%=minutes%> minutes and <%=seconds%> seconds.</p>
</body>
</html>
