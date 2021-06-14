package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.util.Collection;

public interface MealRepository {
    // null if updated meal do not belong to userId
    Meal save(int userId, Meal meal);

    // false if meal do not belong to userId
    boolean delete(int userId, int id);

    // null if meal do not belong to userId
    Meal get(int userId, int id);

    // ORDERED dateTime desc
    Collection<Meal> getAll(int userId);

    Collection<Meal> getByDateFilter(int userId, LocalDate startDate, LocalDate endDate);
}
