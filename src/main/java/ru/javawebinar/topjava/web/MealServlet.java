package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.repository.MealsCrud;
import ru.javawebinar.topjava.repository.MealsCrudInMemory;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private static final int CALORIES_PER_DAY = 2000;
    private MealsCrud mealsCrud;

    public void init() {
        mealsCrud = new MealsCrudInMemory();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        switch (action) {
            case "delete":
                int id = Integer.parseInt(request.getParameter("id"));
                mealsCrud.delete(id);
                log.debug("redirect to meals from GET:delete");
                response.sendRedirect("meals");
                break;
            case "add":
                log.debug("forward to editmeal from GET:add");
                request.getRequestDispatcher("/editmeal.jsp").forward(request, response);
                break;
            case "update":
                id = Integer.parseInt(request.getParameter("id"));
                Meal meal = mealsCrud.getById(id);
                request.setAttribute("meal", meal);
                log.debug("forward to editmeal from GET:update");
                request.getRequestDispatcher("/editmeal.jsp").forward(request, response);
                break;
            case "list":
            default:
                request.setAttribute("list", getListMealTo());
                log.debug("forward to meals from GET:list");
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        Integer id = null;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
        }
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));
        String dateStr = request.getParameter("datetime");
        LocalDateTime dateTime = LocalDateTime.parse(dateStr);

        Meal meal;
        if (id == null) {
            meal = new Meal(0, dateTime, description, calories);
            mealsCrud.create(meal);
        } else {
            meal = new Meal(id, dateTime, description, calories);
            mealsCrud.update(meal);
        }

        log.debug("redirect to meals from POST:create/update");
        response.sendRedirect("meals");
    }

    private List<MealTo> getListMealTo() {
        return MealsUtil.filteredByStreams(mealsCrud.getAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY);
    }
}
