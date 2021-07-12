package ru.javawebinar.topjava.web.meal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@Controller
public class JspMealController extends AbstractMealController {

    public JspMealController(@Autowired MealService service) {
        super(service);
    }

    @GetMapping(value = "/meals")
    public String getAllMeals(Model model) {
        model.addAttribute("meals", getAll());
        return "meals";
    }

    @GetMapping(value = "/updatemeal/{id}")
    public String updateMeal(Model model, @PathVariable int id) {
        model.addAttribute("meal", get(id));
        return "mealForm";
    }

    @GetMapping(value = "/addmeal")
    public String addMeal(Model model) {
        model.addAttribute("meal", new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000));
        return "mealForm";
    }

    @GetMapping(value = "/deletemeal/{id}")
    public String deleteMeal(@PathVariable int id) {
        delete(id);
        return "redirect:/meals";
    }

    @GetMapping(value = "/meals/filter")
    public String getFilteredMeals(Model model,
                                   @RequestParam(value = "startDate", required = false) String startDate,
                                   @RequestParam(value = "endDate", required = false) String endDate,
                                   @RequestParam(value = "startTime", required = false) String startTime,
                                   @RequestParam(value = "endTime", required = false) String endTime) {
        System.out.println(startDate + " " + endDate + " " + startTime + " " + endTime);
        LocalDate sDate = parseLocalDate(startDate);
        LocalDate eDate = parseLocalDate(endDate);
        LocalTime sTime = parseLocalTime(startTime);
        LocalTime eTime = parseLocalTime(endTime);
        model.addAttribute("meals", getBetween(sDate, sTime, eDate, eTime));
        return "meals";
    }

    @PostMapping({"/savemeal", "/updatemeal/savemeal"})
    public String createMeal(HttpServletRequest request) throws UnsupportedEncodingException {
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));
        if (StringUtils.hasLength(request.getParameter("id"))) {
            update(meal, getId(request));
        } else {
            create(meal);
        }
        return "redirect:/meals";
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }
}