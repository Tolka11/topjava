package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));

        List<UserMealWithExcess> mealsOptTwo = filteredByOneCycle(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsOptTwo.forEach(System.out::println);

        System.out.println(filteredByOneStream(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    // HW0 by Cycles
    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> mapCaloriesPerDay = new HashMap<>();
        for (UserMeal userMeal : meals) {
            LocalDate mealDate = userMeal.getDate();
            mapCaloriesPerDay.put(mealDate, userMeal.getCalories() + mapCaloriesPerDay.getOrDefault(mealDate, 0));
        }
        List<UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal userMeal : meals) {
            LocalTime mealTime = userMeal.getTime();
            LocalDate mealDate = userMeal.getDate();
            if (TimeUtil.isBetweenHalfOpen(mealTime, startTime, endTime)) {
                result.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), new AtomicBoolean(mapCaloriesPerDay.get(mealDate) > caloriesPerDay)));
            }
        }
        return result;
    }

    // Stream - Optional 1
    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> mapCaloriesPerDay = meals.stream()
                .collect(Collectors.toMap(UserMeal::getDate, UserMeal::getCalories, Integer::sum));
        return meals.stream()
                .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime))
                .map(meal -> new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), new AtomicBoolean(mapCaloriesPerDay.get(meal.getDate()) > caloriesPerDay)))
                .collect(Collectors.toList());
    }

    // Cycle - Optional 2
    public static List<UserMealWithExcess> filteredByOneCycle(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // Maps for Calories and Excess per Day
        Map<LocalDate, Integer> mapCaloriesPerDay = new HashMap<>();
        Map<LocalDate, AtomicBoolean> mapExcessPerDay = new HashMap<>();
        // Result by once cycle pass
        List<UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal userMeal : meals) {
            LocalTime mealTime = userMeal.getTime();
            LocalDate mealDate = userMeal.getDate();
            // Compute Calories ana Excess per day (excess reference mutable type)
            mapCaloriesPerDay.merge(mealDate, userMeal.getCalories(), Integer::sum);
            mapExcessPerDay.computeIfAbsent(mealDate, k -> new AtomicBoolean(false));
            AtomicBoolean excess = mapExcessPerDay.get(mealDate);
            excess.set(mapCaloriesPerDay.get(mealDate) > caloriesPerDay);
            // Sort by time and create UserMealWithExcess entry
            if (TimeUtil.isBetweenHalfOpen(mealTime, startTime, endTime)) {
                result.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), excess));
            }
        }
        return result;
    }

    // Stream - Optional 2
    public static List<UserMealWithExcess> filteredByOneStream(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // Compute UserMealWithExcess List with our Collector
        return meals.stream()
                .collect(new ExcessCollector(startTime, endTime, caloriesPerDay));
    }


    // Collector for create List<UserMealWithExcess>, with time sorting and compute excess per day
    public static class ExcessCollector implements Collector<UserMeal, ExcessCollector.Holder, List<UserMealWithExcess>> {

        private static LocalTime startTime;
        private static LocalTime endTime;
        private static int caloriesPerDay;

        public ExcessCollector(LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
            ExcessCollector.startTime = startTime;
            ExcessCollector.endTime = endTime;
            ExcessCollector.caloriesPerDay = caloriesPerDay;
        }

        static class Holder {
            final List<UserMealWithExcess> list;                // Result List<UserMealWithExcess>
            final Map<LocalDate, Integer> mapCal;               // Map for compute Calories per day
            final Map<LocalDate, AtomicBoolean> mapExc;         // Map for compute Excess per day

            Holder() {
                this.list = new ArrayList<>();
                this.mapCal = new HashMap<>();
                this.mapExc = new HashMap<>();
            }
        }

        @Override
        public Supplier<Holder> supplier() {
            return Holder::new;
        }

        @Override
        public BiConsumer<Holder, UserMeal> accumulator() {
            return (holder, meal) -> {
                LocalDate mealDate = meal.getDate();
                holder.mapCal.merge(mealDate, meal.getCalories(), Integer::sum);                    // Compute calories per day
                holder.mapExc.computeIfAbsent(mealDate, k -> new AtomicBoolean(false));   // Create new reference mutable Boolean for excess
                AtomicBoolean excess = holder.mapExc.get(mealDate);                                 // Get reference to Excess field
                if (holder.mapCal.get(mealDate) > caloriesPerDay) {
                    excess.set(true);                                                               // Set Excess field
                }
                // Sort by time and create new UserMealWithExcess Entry
                if (TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime)) {
                    holder.list.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess));
                }
            };
        }

        @Override
        public BinaryOperator<Holder> combiner() {
            return (holder1, holder2) -> {
                holder1.list.addAll(holder2.list);
                holder1.mapCal.putAll(holder2.mapCal);
                holder1.mapExc.putAll(holder2.mapExc);
                return holder1;
            };
        }

        @Override
        public Function<Holder, List<UserMealWithExcess>> finisher() {
            return holder -> holder.list;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return EnumSet.of(Characteristics.CONCURRENT);
        }
    }
}
