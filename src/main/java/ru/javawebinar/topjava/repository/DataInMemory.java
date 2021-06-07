package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

// Singleton
public class DataInMemory {
    private static final int CALORIES_PER_DAY = 2000;
    private static volatile AtomicInteger idCount;
    private static List<Meal> meals;

    private static DataInMemory instance;

    private DataInMemory() {
        idCount = new AtomicInteger(0);
        meals = new ArrayList<>(Arrays.asList(
                new Meal(idCount.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new Meal(idCount.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new Meal(idCount.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new Meal(idCount.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new Meal(idCount.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new Meal(idCount.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new Meal(idCount.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        ));
    }

    public synchronized static DataInMemory getInstance() {
        if (instance == null) {
            instance = new DataInMemory();
        }
        return instance;
    }

    public static int getCaloriesPerDay() {
        return CALORIES_PER_DAY;
    }

    public synchronized static List<Meal> getMeals() {
        return meals;
    }

    public synchronized static void setMeals(List<Meal> meals) {
        DataInMemory.meals = meals;
    }

    public synchronized static void addNewMeal(Meal meal) {
        meals.add(new Meal(idCount.incrementAndGet(), meal.getDateTime(), meal.getDescription(), meal.getCalories()));
    }

    public synchronized static int getIdCount() {
        return idCount.get();
    }
}
