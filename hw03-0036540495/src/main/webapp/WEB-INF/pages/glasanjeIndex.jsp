<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="hr">
<head>
    <meta charset="UTF-8">
    <title>Glasanje</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/getcolor">
</head>
<body>
    <h1>Glasanje za omiljeni band:</h1>
    <p>Od sljedećih bendova, koji Vam je band najdraži? Kliknite na link kako biste glasali!</p>
    <ol>
        <%--@elvariable id="bendovi" type="java.util.List<hr.fer.oprpp2.models.Band>"--%>
        <c:forEach var="band" items="${bendovi}">
            <li><a href="glasanje-glasaj?id=${band.id}">${band.name}</a></li>
        </c:forEach>
    </ol>
</body>
</html>
