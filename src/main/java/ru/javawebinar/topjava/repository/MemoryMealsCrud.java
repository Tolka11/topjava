package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MemoryMealsCrud implements MealsCrud {

    private final AtomicInteger idCount;
    private final Map<Integer, Meal> mealsMap;

    public MemoryMealsCrud() {
        idCount = new AtomicInteger(0);
        mealsMap = new ConcurrentHashMap<>();
        create(new Meal(0, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));
        create(new Meal(0, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
        create(new Meal(0, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
        create(new Meal(0, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
        create(new Meal(0, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
        create(new Meal(0, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
        create(new Meal(0, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
    }

    @Override
    public Meal create(Meal meal) {
        int id = idCount.incrementAndGet();
        Meal newMeal = new Meal(id, meal.getDateTime(), meal.getDescription(), meal.getCalories());
        mealsMap.putIfAbsent(id, newMeal);
        return newMeal;
    }

    @Override
    public List<Meal> getAll() {
        return mealsMap.values().stream()
                .sorted(Comparator.comparing(Meal::getDateTime))
                .collect(Collectors.toList());
    }

    @Override
    public Meal getById(int id) {
        return mealsMap.get(id);
    }

    @Override
    public Meal update(Meal meal) {
        Integer id = meal.getId();
        if (id < 1 || id > idCount.get() || id == null) {
            return create(meal);
        }
        mealsMap.replace(id, meal);
        return meal;
    }

    @Override
    public void delete(int id) {
        mealsMap.remove(id);
    }
}
