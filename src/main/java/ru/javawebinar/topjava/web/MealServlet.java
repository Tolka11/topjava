package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.repository.CrudInterface;
import ru.javawebinar.topjava.repository.CrudInMemory;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private CrudInterface crudInterface = new CrudInMemory();
    private static String INSERT_OR_EDIT = "/editmeal.jsp";
    private static String LIST_MEAL = "/meals.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        String forward = "";
        String action = request.getParameter("action");

        if (action == null) {
            log.debug("forward to meals");
            forward = LIST_MEAL;
            request.setAttribute("list", getListMealTo());
        } else if (action.equalsIgnoreCase("delete")) {
            int id = Integer.parseInt(request.getParameter("id"));
            crudInterface.delete(id);
            log.debug("forward to meals");
            forward = LIST_MEAL;
            request.setAttribute("list", getListMealTo());
        } else if (action.equalsIgnoreCase("edit")) {
            int id = Integer.parseInt(request.getParameter("id"));
            Meal meal = crudInterface.readById(id);
            log.debug("forward to editmeal");
            forward = INSERT_OR_EDIT;
            request.setAttribute("meal", meal);
        } else if (action.equalsIgnoreCase("list")) {
            log.debug("forward to meals");
            forward = LIST_MEAL;
            request.setAttribute("list", getListMealTo());
        } else {
            log.debug("forward to editmeal");
            forward = INSERT_OR_EDIT;
        }

        request.getRequestDispatcher(forward).forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        int id = Integer.parseInt(request.getParameter("id"));
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));
        String dateStr = request.getParameter("datetime");
        LocalDateTime dateTime = LocalDateTime.parse(dateStr);

        Meal meal = new Meal(id, dateTime, description, calories);

        if (id <= 0 || id > crudInterface.getIdCount()) {
            crudInterface.create(meal);
        } else {
            crudInterface.update(id, meal);
        }

        log.debug("forward to meals");
        request.setAttribute("list", getListMealTo());
        request.getRequestDispatcher(LIST_MEAL).forward(request, response);
    }

    private List<MealTo> getListMealTo() {
        List<Meal> meals = crudInterface.read().stream()
                .sorted(Comparator.comparing(Meal::getDateTime))
                .collect(Collectors.toList());
        return MealsUtil.filteredByStreams(meals, LocalTime.MIN, LocalTime.MAX, crudInterface.getCaloriesLimit());
    }

}
