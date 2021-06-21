package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class MealTestData {
    public static final int ID = 100003;
    public static final int USER_ID = 100000;
    public static final int ADMIN_ID = 100001;

    public static final Meal meal02 = new Meal(100002, LocalDateTime.parse("2020-01-30T10:00:00"), "Завтрак", 500);
    public static final Meal meal03 = new Meal(ID, LocalDateTime.parse("2020-01-30T13:00:00"), "Обед", 1000);
    public static final Meal meal04 = new Meal(100004, LocalDateTime.parse("2020-01-30T20:00:00"), "Ужин", 500);
    public static final Meal meal09 = new Meal(100009, LocalDateTime.parse("2020-01-31T14:00:00"), "Админ ланч", 510);
    public static final Meal meal10 = new Meal(100010, LocalDateTime.parse("2020-01-31T21:00:00"), "Админ ужин", 1500);

    public static Meal getNew() {
        return new Meal(null, LocalDateTime.parse("2020-02-02T20:02:00"), "Админ перекус", 111);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(meal03);
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
        assertThat(actual).isEqualTo(expected);
    }


}
