package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface CrudInterface {

    void create(Meal meal);

    List<Meal> read();

    Meal readById(int id);

    void update(int id, Meal newMeal);

    void delete(int id);

    int getCaloriesLimit();

    int getIdCount();
}
