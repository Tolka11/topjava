package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.Util;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Profile("datajpa")
public class DataJpaMealRepository implements MealRepository {
    private final CrudMealRepository crudMealRepository;
    private final CrudUserRepository crudUserRepository;

    public DataJpaMealRepository(CrudMealRepository crudMealRepository, CrudUserRepository crudUserRepository) {
        this.crudMealRepository = crudMealRepository;
        this.crudUserRepository = crudUserRepository;
    }

    @Override
    public Meal save(Meal meal, int userId) {
        meal.setUser(crudUserRepository.getById(userId));
        if (!meal.isNew() && get(meal.id(), userId) == null) {
            return null;
        }
        return crudMealRepository.save(meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        return crudMealRepository.delete(id, userId) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        return crudMealRepository.findMealByIdAndUserId(id, userId);
    }


    @Override
    public List<Meal> getAll(int userId) {
        return crudMealRepository.findAllByUserIdOrderByDateTimeDesc(userId);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return getAll(userId).stream()
                .filter(meal -> Util.isBetweenHalfOpen(meal.getDateTime(), startDateTime, endDateTime))
                .collect(Collectors.toList());
    }


    @Override
    public Meal getWithUser(int id, int userId) {
        Meal meal = crudMealRepository.findMealByIdAndUserId(id, userId);
        if (meal != null) {
            meal.setUser(crudUserRepository.findById(userId).orElse(null));
        }
        return meal;
    }
}
