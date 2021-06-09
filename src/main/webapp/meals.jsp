<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="ru">
<head>
    <title>Meal list</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>

<h4><a href="meals?action=add">Add meal</a></h4>

<table border="1" cellspacing="0" cellpadding="10" class="table">
    <tr>
        <th scope="col">Date</th>
        <th scope="col">Description</th>
        <th scope="col">Calories</th>
        <th scope="col"></th>
        <th scope="col"></th>
    </tr>

    <c:forEach items="${list}" var="mealTo">
        <tr style="color: ${mealTo.excess ? 'red' : 'green'}">
            <td>${mealTo.dateTime.toString().replace("T", " ")}</td>
            <td>${mealTo.description}</td>
            <td>${mealTo.calories}</td>
            <td>
                <a href="meals?action=update&id=${mealTo.id}">Update</a>
            </td>
            <td><a href="meals?action=delete&id=${mealTo.id}">Delete</a></td>
        </tr>
    </c:forEach>
</table>

</body>
</html>