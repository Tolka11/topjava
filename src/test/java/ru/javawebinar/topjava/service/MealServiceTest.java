package ru.javawebinar.topjava.service;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    private static List<String> runTimes = new ArrayList<>();

    @Autowired
    private MealService service;

    @Rule
    public RunTimeRule rule = new RunTimeRule();

    @AfterClass
    public static void after() {
        for (String str : runTimes) {
            System.out.println(str);
        }
    }

    @Test
    public void delete() {
        service.delete(MEAL1_ID, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(MEAL1_ID, USER_ID));
    }

    @Test
    public void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND, USER_ID));
    }

    @Test
    public void deleteNotOwn() {
        assertThrows(NotFoundException.class, () -> service.delete(MEAL1_ID, ADMIN_ID));
    }

    @Test
    public void create() {
        Meal created = service.create(getNew(), USER_ID);
        int newId = created.id();
        Meal newMeal = getNew();
        newMeal.setId(newId);
        MATCHER.assertMatch(created, newMeal);
        MATCHER.assertMatch(service.get(newId, USER_ID), newMeal);
    }

    @Test
    public void duplicateDateTimeCreate() {
        assertThrows(DataAccessException.class, () ->
                service.create(new Meal(null, meal1.getDateTime(), "duplicate", 100), USER_ID));
    }


    @Test
    public void get() {
        Meal actual = service.get(ADMIN_MEAL_ID, ADMIN_ID);
        MATCHER.assertMatch(actual, adminMeal1);
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND, USER_ID));
    }

    @Test
    public void getNotOwn() {
        assertThrows(NotFoundException.class, () -> service.get(MEAL1_ID, ADMIN_ID));
    }

    @Test
    public void update() {
        Meal updated = getUpdated();
        service.update(updated, USER_ID);
        MATCHER.assertMatch(service.get(MEAL1_ID, USER_ID), getUpdated());
    }

    @Test
    public void updateNotOwn() {
        assertThrows(NotFoundException.class, () -> service.update(meal1, ADMIN_ID));
        MATCHER.assertMatch(service.get(MEAL1_ID, USER_ID), meal1);
    }

    @Test
    public void getAll() {
        MATCHER.assertMatch(service.getAll(USER_ID), meals);
    }

    @Test
    public void getBetweenInclusive() {
        MATCHER.assertMatch(service.getBetweenInclusive(
                LocalDate.of(2020, Month.JANUARY, 30),
                LocalDate.of(2020, Month.JANUARY, 30), USER_ID),
                meal3, meal2, meal1);
    }

    @Test
    public void getBetweenWithNullDates() {
        MATCHER.assertMatch(service.getBetweenInclusive(null, null, USER_ID), meals);
    }

    public class RunTimeRule implements TestRule {
        @Override
        public Statement apply(final Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    LocalDateTime from = LocalDateTime.now();
                    base.evaluate();
                    LocalDateTime to = LocalDateTime.now();

                    String str = description.getMethodName() + " -" + testRunTime(from, to);
                    System.out.println("************ The execution time of test method " + str);
                    runTimes.add(str);
                }
            };
        }

        private String testRunTime(LocalDateTime from, LocalDateTime to) {
            long hours = ChronoUnit.HOURS.between(from, to);
            long minutes = ChronoUnit.MINUTES.between(from, to);
            long seconds = ChronoUnit.SECONDS.between(from, to);
            long milliseconds = ChronoUnit.MILLIS.between(from, to);
            long nano = ChronoUnit.NANOS.between(from, to);

            return (" " +                           // Зачем заморачивался не знаю, достаточно было миллисекунд и нано в скобках ;)
                    (hours == 0 ? "" : hours + " hours ") +
                    (minutes == 0 ? "" : minutes % (hours == 0 ? Long.MAX_VALUE : hours * 60) + " minutes ") +
                    (seconds == 0 ? "" : seconds % (minutes == 0 ? Long.MAX_VALUE : minutes * 60) + " seconds ") +
                    (milliseconds == 0 ? "" : milliseconds % (seconds == 0 ? Long.MAX_VALUE : seconds * 1000) + " milliseconds ") +
                    (nano == 0 ? "" : "(" + nano + " nano)"));
        }
    }
}