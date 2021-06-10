<%@ page contentType="text/html;charset=UTF-8" %>

<html lang="ru">
<head>
    <title>Meal</title>
</head>

<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Edit meal</h2>

<form method="POST" action='meals' name="frmEditMeal">

    <input type="hidden" name="id" value="${meal.id}"/>

    <table border="0">

        <tr>
            <td><b>DateTime:</b></td>
            <td><input id="datetime" type="datetime-local" name="datetime"
                       value="${meal.dateTime.toString()}"/>
            </td>
        </tr>
        <tr>
            <td><b>Description:</b></td>
            <td><input type="text" name="description"
                       value="${meal.description}"/>
            </td>
        </tr>
        <tr>
            <td><b>Calories: </b></td>
            <td><input type="number" name="calories"
                       value="${meal.calories}"/>
            </td>
        </tr>

    </table>

    <input type="submit" value="Save"/>
    <input type="button" onclick="window.location.href='meals';" value="Cancel">

</form>

</body>
</html>