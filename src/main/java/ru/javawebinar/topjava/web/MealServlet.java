package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.repository.DataInMemory;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("forward to meals");

        DataInMemory dim = DataInMemory.getInstance();
        List<MealTo> result = MealsUtil.filteredByStreams(dim.getMeals(), LocalTime.of(0, 0, 0),
                LocalTime.of(23, 59, 59, 999999999), dim.getCaloriesPerDay());

        request.setAttribute("list", result);
        request.getRequestDispatcher("/meals.jsp").forward(request, response);
    }
}
