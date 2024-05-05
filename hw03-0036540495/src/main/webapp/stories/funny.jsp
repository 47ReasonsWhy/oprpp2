<%@ page import="hr.fer.oprpp2.util.RandomFontColorChooser" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>A funny story</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/getcolor">
    <style>
        body {
            color: <%= RandomFontColorChooser.choose() %>;
        }
    </style>
</head>
<body>
<h1>A funny story</h1>
<div style="text-align: justify; text-justify: inter-word;">
    When I was little, I would go on Nickelodeon.com all the time, and they had this game similar to Club Penguin,
    but it was called Nicktropolis. And if you forgot your password, a security question you could choose was&
    "What is your eye color?". And if you got it right, it'd tell you your password. So I would go to popular
    locations in Nicktropolis and write down random usernames who were also in those areas, and then I would log out
    and type in the username as if it were my own and see which of these accounts also had "What is your eye color?"
    set as their security question (which was most of them, since it was easy, and we were all kids). And then I'd
    try either brown, blue or green, and always get in. Then I would go to their house and send all of their furniture
    and decorations to my own accounts. And if I didn't want it, I could sell it for money. I got pretty good at it
    and I had over 200 items in my house, it was incredibly fun. And that's what started me on my path to being a
    blackhat hacker.
</div>
</body>
</html>
