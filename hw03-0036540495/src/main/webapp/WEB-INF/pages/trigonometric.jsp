<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Trigonometry table</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/getcolor">
</head>
<body>
    <h1>Trigonometry table</h1>
    <table border="1">
        <tr>
            <th>Angle(°)</th>
            <th>Sine</th>
            <th>Cosine</th>
        </tr>
        <%--@elvariable id="sinValues" type="java.util.Map<java.lang.Integer, java.lang.String>"--%>
        <%--@elvariable id="cosValues" type="java.util.Map<java.lang.Integer, java.lang.String>"--%>
        <c:forEach var="i" items="${sinValues.keySet()}">
            <tr>
                <td>${i}</td>
                <td>${sinValues.get(i)}</td>
                <td>${cosValues.get(i)}</td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>
