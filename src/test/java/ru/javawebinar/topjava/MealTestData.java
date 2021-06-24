package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final int USER_MEAL_ID = START_SEQ + 3;

    public static final Meal userMeal2 = new Meal(START_SEQ + 2, LocalDateTime.of(2020, 01, 30, 10, 00, 00), "Завтрак", 500);
    public static final Meal userMeal3 = new Meal(USER_MEAL_ID, LocalDateTime.of(2020, 01, 30, 13, 00, 00), "Обед", 1000);
    public static final Meal userMeal4 = new Meal(START_SEQ + 4, LocalDateTime.of(2020, 01, 30, 20, 00, 00), "Ужин", 500);
    public static final Meal adminMeal9 = new Meal(START_SEQ + 9, LocalDateTime.of(2020, 01, 31, 14, 00, 00), "Админ ланч", 510);
    public static final Meal adminMeal10 = new Meal(START_SEQ + 10, LocalDateTime.of(2020, 01, 31, 21, 00, 00), "Админ ужин", 1500);

    public static Meal getNew() {
        return new Meal(null, LocalDateTime.of(2020, 02, 02, 20, 02, 00), "Админ перекус", 111);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(userMeal3);
        updated.setDateTime(LocalDateTime.of(2021, 03, 03, 20, 02, 20));
        updated.setDescription("Обновлённый обед");
        updated.setCalories(333);
        return updated;
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
