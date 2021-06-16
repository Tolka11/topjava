package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryMealRepository.class);

    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> save(meal.getUserId(), meal));
    }

    @Override
    public Meal save(int userId, Meal meal) {
        log.info("save {}", meal);
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            return repository.computeIfAbsent(meal.getId(), k -> meal);
        }
        // null if existing updating meal do not belong to userId
        Integer userIdInMeal = getUserIdFromMealInRep(meal.getId());
        if (userId != userIdInMeal) {
            return null;
        }
        // handle case: update, but not present in storage
        return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int userId, int id) {
        log.info("delete {}", id);
        // false if meal do not belong to userId
        Integer userIdInMeal = getUserIdFromMealInRep(id);
        if (userId != userIdInMeal) {
            return false;
        }
        return repository.remove(id) != null;
    }

    @Override
    public Meal get(int userId, int id) {
        log.info("get {}", id);
        // null if meal do not belong to userId
        Meal meal = repository.get(id);
        if (meal == null) {
            return null;
        }
        return (userId == meal.getUserId() ? meal : null);
    }

    @Override
    public List<Meal> getAll(int userId) {
        log.info("getAll");
        // ORDERED dateTime desc
        return getByUserId(userId).stream()
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Meal> getByDateFilter(int userId, LocalDate startDate, LocalDate endDate) {
        log.info("getByDateFilter");
        return getByUserId(userId).stream()
                .filter(meal -> !meal.getDate().isBefore(startDate))    // from date (including)
                .filter(meal -> !meal.getDate().isAfter(endDate))       // to date (including)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    private List<Meal> getByUserId(int userId) {
        return repository.values().stream()
                .filter(meal -> userId == meal.getUserId())
                .collect(Collectors.toList());
    }

    private Integer getUserIdFromMealInRep(int id) {
        try {
            return repository.get(id).getUserId();
        } catch (NullPointerException npe) {
            return null;
        }
    }
}

