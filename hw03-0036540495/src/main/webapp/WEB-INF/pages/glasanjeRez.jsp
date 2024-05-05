<%@ page contentType="text/html;charset=UTF-8"  pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="hr">
<head>
    <meta charset="UTF-8">
    <title>Rezultati glasanja</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/getcolor">
    <style>
        table.rez td {text-align: center;}
    </style>
</head>
<body>
    <h1>Rezultati glasanja</h1>
    <p>Ovo su rezultati glasanja.</p>
    <table border="1">
        <thead><tr><th>Bend</th><th>Broj glasova</th></tr></thead>
        <tbody>
            <%--@elvariable id="bendovi" type="java.util.List<hr.fer.oprpp2.models.Band>"--%>
            <c:forEach var="band" items="${bendovi}">
                <tr><td>${band.name}</td><td>${band.votes}</td></tr>
            </c:forEach>
        </tbody>
    </table>
    <h2>Grafički prikaz rezultata</h2>
    <img alt="Pie-chart" src="${pageContext.request.contextPath}/glasanje-grafika" width="400" height="400" />
    <h2>Rezultati u XLS formatu</h2>
    <p>Rezultati u XLS formatu dostupni su <a href="${pageContext.request.contextPath}/glasanje-xls">ovdje.</a></p>
    <h2>Razno</h2>
    <p>Primjeri pjesama pobjedničkih bendova:</p>
    <ul>
        <%--@elvariable id="pobjednici" type="java.util.List<hr.fer.oprpp2.models.Band>"--%>
        <c:forEach var="band" items="${pobjednici}">
            <li><a href="${band.link}" target="_blank">${band.name}</a></li>
        </c:forEach>
    </ul>
</body>
</html>
