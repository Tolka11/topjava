package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

public class DataInMemory {
    static final int CALORIES_PER_DAY = 2000;
    private static List<Meal> meals;

    private static DataInMemory instance;

    private DataInMemory() {
        meals = Arrays.asList(
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );
    }

    public static DataInMemory getInstance() {
        if (instance == null) {
            instance = new DataInMemory();
        }
        return instance;
    }

    public static int getCaloriesPerDay() {
        return CALORIES_PER_DAY;
    }

    public static List<Meal> getMeals() {
        return meals;
    }

    public static void setMeals(List<Meal> meals) {
        DataInMemory.meals = meals;
    }
}
