package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public class CrudInMemory implements CrudInterface {

    DataInMemory dim = DataInMemory.getInstance();

    @Override
    public void create(Meal meal) {
        dim.addNewMeal(meal);
    }

    @Override
    public List<Meal> read() {
        return dim.getMeals();
    }

    @Override
    public Meal readById(int id) {
        List<Meal> meals = read();
        for (Meal meal : meals) {
            if (meal.getId() == id) {
                return meal;
            }
        }
        return null;
    }

    @Override
    public void update(int id, Meal newMeal) {
        List<Meal> list = read();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == id) {
                list.remove(i);
                list.add(new Meal(id, newMeal.getDateTime(), newMeal.getDescription(), newMeal.getCalories()));
                return;
            }
        }
    }

    @Override
    public void delete(int id) {
        List<Meal> list = read();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == id) {
                list.remove(i);
                return;
            }
        }
    }

    @Override
    public int getCaloriesLimit() {
        return dim.getCaloriesPerDay();
    }

    @Override
    public int getIdCount() {
        return dim.getIdCount();
    }
}
