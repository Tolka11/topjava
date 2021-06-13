package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.Collection;
import java.util.Comparator;
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
        MealsUtil.meals.forEach(meal -> {
            Integer id = counter.incrementAndGet();
            meal.setId(id);
            repository.put(id, meal);
            return;
        });
    }

    @Override
    public Meal save(Meal meal) {
        log.info("save {}", meal);
        // null if created/updated meal do not belong to userId
        if (SecurityUtil.authUserId() != meal.getUserId()) {
            return null;
        }
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            return repository.computeIfAbsent(meal.getId(), k -> meal);
        }
        // null if existing updating meal do not belong to userId
        if (SecurityUtil.authUserId() != get(meal.getId()).getUserId()) {
            return null;
        }
        // handle case: update, but not present in storage
        return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int id) {
        log.info("delete {}", id);
        // false if meal do not belong to userId
        if (get(id).getUserId() != SecurityUtil.authUserId()) {
            return false;
        }
        return repository.remove(id) != null;
    }

    @Override
    public Meal get(int id) {
        log.info("get {}", id);
        // null if meal do not belong to userId
        Meal meal = repository.get(id);
        return (meal.getUserId() == SecurityUtil.authUserId() ? meal : null);
    }

    @Override
    public Collection<Meal> getAll() {
        log.info("getAll");
        // ORDERED dateTime desc
        return repository.values().stream()
                .filter(meal -> meal.getUserId() == SecurityUtil.authUserId())
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}

