package ru.javawebinar.topjava;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.service.UserService;

public class SpringMain {
    public static void main(String[] args) {
        // java 7 automatic resource management (ARM)

        try (ConfigurableApplicationContext appCtx = new ClassPathXmlApplicationContext();) {
            ConfigurableEnvironment env = appCtx.getEnvironment();
            env.setActiveProfiles(Profiles.getActiveProfiles());
            ((ClassPathXmlApplicationContext) appCtx).setConfigLocations("spring/spring-app.xml", "spring/spring-db.xml");
            appCtx.refresh();

//            System.out.println("Bean definition names: " + Arrays.toString(appCtx.getBeanDefinitionNames()));
//            AdminRestController adminUserController = appCtx.getBean(AdminRestController.class);
//            adminUserController.create(new User(null, "userName", "email@mail.ru", "password", Role.ADMIN));
//            System.out.println();
//
//            MealRestController mealController = appCtx.getBean(MealRestController.class);
//            List<MealTo> filteredMealsWithExcess =
//                    mealController.getBetween(
//                            LocalDate.of(2020, Month.JANUARY, 30), LocalTime.of(7, 0),
//                            LocalDate.of(2020, Month.JANUARY, 31), LocalTime.of(11, 0));
//            filteredMealsWithExcess.forEach(System.out::println);
//            System.out.println();
//            System.out.println(mealController.getBetween(null, null, null, null));

            System.out.println("\n**************************************************\n");

            MealService mealService = appCtx.getBean(MealService.class);
            Meal meal = mealService.getWithUser(100002, 100000);
            System.out.println(meal);
            System.out.println(meal.getUser());
            meal = mealService.getWithUser(100010, 100001);
            System.out.println(meal);
            System.out.println(meal.getUser());

            System.out.println("\n**************************************************\n");

            UserService userService = appCtx.getBean(UserService.class);
            User user = userService.getWithMeals(100001);
            System.out.println(user);
            System.out.println(user.getMeals());
            System.out.println("**************************************************");
            System.out.println(UserTestData.adminWithMeals);
            System.out.println(UserTestData.adminWithMeals.getMeals());
        }
    }
}
