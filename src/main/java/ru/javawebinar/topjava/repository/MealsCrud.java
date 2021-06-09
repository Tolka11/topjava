package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealsCrud {

    Meal create(Meal meal);

    List<Meal> getAll();

    Meal getById(int id);

    Meal update(Meal newMeal);

    void delete(int id);
}
