package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

@Controller
public class MealRestController {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final MealService service;

    public MealRestController(@Autowired MealService service) {
        this.service = service;
    }

    public List<Meal> getAll() {
        log.info("getAll");
        return (List<Meal>) service.getAll();
    }

    public List<Meal> getByFilter(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        log.info("getByFilter");
        return getAll().stream()
                .filter(meal -> !meal.getDate().isBefore(startDate == null ? LocalDate.MIN : startDate))    // from date (including)
                .filter(meal -> !meal.getDate().isAfter(endDate == null ? LocalDate.MAX : endDate))         // to date (including)
                .filter(meal -> !meal.getTime().isBefore(startTime == null ? LocalTime.MIN : startTime))    // from time (including)
                .filter(meal -> meal.getTime().isBefore(endTime == null ? LocalTime.MAX : endTime))         // to time (excluding)
                .collect(Collectors.toList());
    }

    public Meal get(int id) {
        log.info("get {}", id);
        return service.get(id);
    }

    public Meal create(Meal meal) {
        log.info("create {}", meal);
        checkNew(meal);
        return service.create(meal);
    }

    public void delete(int id) {
        log.info("delete {}", id);
        service.delete(id);
    }

    public void update(Meal meal, int id) {
        log.info("update {} with id={}", meal, id);
        assureIdConsistent(meal, id);
        service.update(meal);
    }
}