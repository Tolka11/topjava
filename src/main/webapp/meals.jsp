<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="ru">
<head>
    <title>Meals</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>

<table border="1" cellspacing="0" cellpadding="10" class="table">
    <tr>
        <th scope="col">Date</th>
        <th scope="col">Description</th>
        <th scope="col">Calories</th>
        <th scope="col"></th>
        <th scope="col"></th>
    </tr>

    <c:forEach items="${list}" var="meal">
        <tr style="color: ${meal.excess ? 'red' : 'green'}">
            <td>${meal.dateTime.toString().replace("T", " ")}</td>
            <td>${meal.description}</td>
            <td>${meal.calories}</td>
            <td></td>
            <td></td>
        </tr>
    </c:forEach>
</table>

</body>
</html>