<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://topjava.javawebinar.ru/functions" %>
<%--<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>--%>
<html>
<head>
    <title>Meal list</title>
    <style>
        .normal {
            color: green;
        }

        .excess {
            color: red;
        }
    </style>
</head>
<body>
<section>
    <h3><a href="index.html">Home</a></h3>
    <hr/>
    <h2>Meals</h2>

    <form method="get" action="meals">
        <input type="hidden" id="action" name="action" value="filter">
        <table border="0" cellpadding="10" cellspacing="0">
            <tr>
                <th>
                    От даты (включая)<br/>
                    <input type="date" name="startDate" id="startDate">
                </th>
                <th>
                    До даты (включая)<br/>
                    <input type="date" name="endDate" id="endDate">
                </th>
                <th width="50"></th>
                <th>
                    От времени (включая)<br/>
                    <input type="time" name="startTime" id="startTime">
                </th>
                <th>
                    До времени (исключая)<br/>
                    <input type="time" name="endTime" id="endTime">
                </th>
            </tr>
            <tr>
                <td colspan="5" align="right">
                    <br/>
                    <div class="card-footer text-right">
                        <input type="button" onclick="window.location.href='meals'" value="Отменить">
                        <button type="submit">Отфильтровать</button>
                    </div>
                </td>
            </tr>
        </table>
    </form>

    <br/>
    <br/>
    <input type="button" onclick="window.location.href='meals?action=create';" value="Add meal">

    <br><br>
    <table border="1" cellpadding="8" cellspacing="0">
        <thead>
        <tr>
            <th>Date</th>
            <th>Description</th>
            <th>Calories</th>
            <th></th>
            <th></th>
        </tr>
        </thead>
        <c:forEach items="${meals}" var="meal">
            <jsp:useBean id="meal" type="ru.javawebinar.topjava.to.MealTo"/>
            <tr class="${meal.excess ? 'excess' : 'normal'}">
                <td>
                        <%--${meal.dateTime.toLocalDate()} ${meal.dateTime.toLocalTime()}--%>
                        <%--<%=TimeUtil.toString(meal.getDateTime())%>--%>
                        <%--${fn:replace(meal.dateTime, 'T', ' ')}--%>
                        ${fn:formatDateTime(meal.dateTime)}
                </td>
                <td>${meal.description}</td>
                <td>${meal.calories}</td>
                <td><a href="meals?action=update&id=${meal.id}">Update</a></td>
                <td><a href="meals?action=delete&id=${meal.id}">Delete</a></td>
            </tr>
        </c:forEach>
    </table>
</section>
</body>
</html>