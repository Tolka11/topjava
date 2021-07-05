package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.User;

import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.STRONG_MATCHER;

@ActiveProfiles("datajpa")
public class DataJpaUserServiceTest extends AbstractUserTest {

    @Test
    public void getWithMeals() {
        User user = service.getWithMeals(ADMIN_ID);
        STRONG_MATCHER.assertMatch(user, UserTestData.adminWithMeals);
    }
}
