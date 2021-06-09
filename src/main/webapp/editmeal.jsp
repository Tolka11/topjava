<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html lang="ru">
<head>
    <title>Meal</title>
</head>

<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Edit meal</h2>

<form method="POST" action='meals' name="frmEditMeal">

    <input type="hidden" name="id" value="<c:out value="${(meal.id!=null)?meal.id:null}" />"/>

    <table border="0">

        <tr>
            <td><b>DateTime:</b></td>
            <td><input id="datetime" type="datetime-local" name="datetime"
                       value="<c:out value="${((meal.dateTime!=null)?meal.dateTime.toString():null)}" />"/>
            </td>
        </tr>
        <tr>
            <td><b>Description:</b></td>
            <td><input type="text" name="description"
                       value="<c:out value="${(meal.description!=null)?meal.description:null}" />"/>
            </td>
        </tr>
        <tr>
            <td><b>Calories: </b></td>
            <td><input type="number" name="calories"
                       value="<c:out value="${(meal.calories!=null)?meal.calories:null}" />"/>
            </td>
        </tr>

    </table>

    <input type="submit" value="Save"/>
    <input type="button" onclick="window.location.href='meals';" value="Cancel">

</form>

</body>
</html>