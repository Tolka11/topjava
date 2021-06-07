<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html lang="ru">
<head>
    <title>Meal</title>
</head>

<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Edit meal</h2>

<form method="POST" action='meals' name="frmEditMeal">

    <input type="hidden" name="id" value="<%=(request.getParameter("id")!=null)?request.getParameter("id"):"0"%>"/>

    <table border="0">

        <tr>
            <td><b>DateTime:</b></td>
            <td><input id="datetime" type="datetime-local" name="datetime"
                       value="<%=(request.getParameter("datetime")!=null)?request.getParameter("datetime"):"2002-02-02T02:02"%>"/>
            </td>
        </tr>
        <tr>
            <td><b>Description:</b></td>
            <td><input type="text" name="description"
                       value="<%=(request.getParameter("description")!=null)?request.getParameter("description"):"Введите описание"%>"/>
            </td>
        </tr>
        <tr>
            <td><b>Calories: </b></td>
            <td><input type="text" name="calories"
                       value="<%=(request.getParameter("calories")!=null)?request.getParameter("calories"):"Введите калории"%>"/>
            </td>
        </tr>

    </table>

    <input type="submit" value="Save"/>
    <input type="reset" value="Reset"/>
    <input type="button" onclick="window.location.href='/topjava/meals';" value="Cancel">

</form>

</body>
</html>