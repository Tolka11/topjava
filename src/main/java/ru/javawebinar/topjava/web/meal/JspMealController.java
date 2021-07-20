package ru.javawebinar.topjava.web.meal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@Controller
@RequestMapping("/meals")
public class JspMealController extends AbstractMealController {

    public JspMealController(@Autowired MealService service) {
        super(service);
    }

    @GetMapping("")
    public String getAllJsp(Model model) {
        model.addAttribute("meals", getAll());
        return "meals";
    }

    @GetMapping("/update")
    public String updateJsp(Model model, @RequestParam("id") String sid) {
        int id = Integer.parseInt(sid);
        model.addAttribute("meal", get(id));
        model.addAttribute("action", "update");
        return "mealForm";
    }

    @GetMapping("/add")
    public String addJsp(Model model) {
        model.addAttribute("meal", new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000));
        model.addAttribute("action", "add");
        return "mealForm";
    }

    @GetMapping("/delete")
    public String deleteJsp(@RequestParam("id") String sid) {
        int id = Integer.parseInt(sid);
        delete(id);
        return "redirect:/meals";
    }

    @GetMapping("/filter")
    public String getFilteredJsp(Model model,
                                 @RequestParam(value = "startDate", required = false) String startDate,
                                 @RequestParam(value = "endDate", required = false) String endDate,
                                 @RequestParam(value = "startTime", required = false) String startTime,
                                 @RequestParam(value = "endTime", required = false) String endTime) {
        LocalDate sDate = parseLocalDate(startDate);
        LocalDate eDate = parseLocalDate(endDate);
        LocalTime sTime = parseLocalTime(startTime);
        LocalTime eTime = parseLocalTime(endTime);
        model.addAttribute("meals", getBetween(sDate, sTime, eDate, eTime));
        return "meals";
    }

    @PostMapping("")
    public String createJsp(HttpServletRequest request) {
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