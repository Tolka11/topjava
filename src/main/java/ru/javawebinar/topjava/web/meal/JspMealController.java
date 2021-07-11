package ru.javawebinar.topjava.web.meal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.service.UserService;
import ru.javawebinar.topjava.web.SecurityUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@Controller
public class JspMealController extends AbstractMealController {
    public JspMealController(MealService service) {
        super(service);
    }

//    //*******************************************
//
//    @GetMapping("/")
//    public String root() {
//        return "index";
//    }
//
//    @GetMapping("/users")
//    public String getUsers(Model model) {
//        model.addAttribute("users", service.getAll());
//        return "users";
//    }
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String action = request.getParameter("action");
//
//        switch (action == null ? "all" : action) {
//            case "delete" -> {
//                int id = getId(request);
//                mealController.delete(id);
//                response.sendRedirect("meals");
//            }
//            case "create", "update" -> {
//                final Meal meal = "create".equals(action) ?
//                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
//                        mealController.get(getId(request));
//                request.setAttribute("meal", meal);
//                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
//            }
//            case "filter" -> {
//                LocalDate startDate = parseLocalDate(request.getParameter("startDate"));
//                LocalDate endDate = parseLocalDate(request.getParameter("endDate"));
//                LocalTime startTime = parseLocalTime(request.getParameter("startTime"));
//                LocalTime endTime = parseLocalTime(request.getParameter("endTime"));
//                request.setAttribute("meals", mealController.getBetween(startDate, startTime, endDate, endTime));
//                request.getRequestDispatcher("/meals.jsp").forward(request, response);
//            }
//            default -> {
//                request.setAttribute("meals", mealController.getAll());
//                request.getRequestDispatcher("/meals.jsp").forward(request, response);
//            }
//        }
//    }
//
//    //*******************************************
//
//    @PostMapping("/users")
//    public String setUser(HttpServletRequest request) {
//        int userId = Integer.parseInt(request.getParameter("userId"));
//        SecurityUtil.setAuthUserId(userId);
//        return "redirect:meals";
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        request.setCharacterEncoding("UTF-8");
//        Meal meal = new Meal(
//                LocalDateTime.parse(request.getParameter("dateTime")),
//                request.getParameter("description"),
//                Integer.parseInt(request.getParameter("calories")));
//
//        if (StringUtils.hasLength(request.getParameter("id"))) {
//            mealController.update(meal, getId(request));
//        } else {
//            mealController.create(meal);
//        }
//        response.sendRedirect("meals");
//    }

    //*******************************************




    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }

}
