<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Index</title>
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/getcolor">
</head>
<body>
  <a href="colors.jsp">Background color chooser</a>
  <hr/>
  <a href="trigonometric?a=0&b=90">Trigonometry table (a=0, b=90)</a>
  <hr/>
  <form action="trigonometric" method="GET">
    Početni kut:<br><label>
      <input type="number" name="a" min="0" max="360" step="1" value="0">
    </label><br>
    Završni kut:<br><label>
    <input type="number" name="b" min="0" max="360" step="1" value="360">
      </label><br><br>
    <input type="submit" value="Tabeliraj">&nbsp;<input type="reset" value="Reset">
  </form>
  <hr/>
  <a href="stories/funny.jsp">Funny story</a>
  <hr/>
  <a href="powers?a=1&b=100&n=3">Powers table (a=1, b=100, n=3)</a>
  <hr/>
  <a href="appinfo.jsp">Application info</a>
  <hr/>
  <a href="glasanje">Voting</a>
  <hr/>
</body>
</html>