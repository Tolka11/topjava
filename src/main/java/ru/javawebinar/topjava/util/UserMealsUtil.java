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
        // Карта где подсчитываем калории за каждый день
        Map<LocalDate, Integer> mapCaloriesPerDay = new HashMap<>();
        for (UserMeal userMeal : meals) {
            LocalDate date = userMeal.getDate();
            mapCaloriesPerDay.put(date, userMeal.getCalories() + mapCaloriesPerDay.getOrDefault(date, 0));
        }
        // Формируем лист на выдачу
        List<UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal userMeal : meals) {
            LocalTime time = userMeal.getTime();
            LocalDate date = userMeal.getDate();
            int excess = mapCaloriesPerDay.get(date) - caloriesPerDay;
            if (TimeUtil.isBetweenHalfOpen(time, startTime, endTime)) {
                result.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), new AtomicBoolean(excess > 0)));
            }
        }
        return result;
    }

    // Stream - Optional 1
    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // Карта где подсчитываем калории за каждый день, заполняем через стрим
        Map<LocalDate, Integer> mapCaloriesPerDay = meals.stream()
                .collect(Collectors.toMap(UserMeal::getDate, UserMeal::getCalories, Integer::sum));
        // Формируем лист на выдачу
        return meals.stream()
                .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime))
                .map(meal -> new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), new AtomicBoolean(mapCaloriesPerDay.get(meal.getDate()) - caloriesPerDay > 0)))
                .collect(Collectors.toList());
    }

    // Cycle - Optional 2
    public static List<UserMealWithExcess> filteredByOneCycle(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // Карта где подсчитываем калории за каждый день, и карта где хранится ссылка на дневное превышение калорий
        Map<LocalDate, Integer> mapCaloriesPerDay = new HashMap<>();
        Map<LocalDate, AtomicBoolean> mapExcessPerDay = new HashMap<>();
        // Формируем лист на выдачу
        List<UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal userMeal : meals) {
            LocalTime time = userMeal.getTime();
            LocalDate date = userMeal.getDate();
            // По дате вычисляем сумму калорий в одной карте и по этой сумме калорий определяем превышение на данную дату в другой карте,
            // тип поля превышения ссылочный, после прохода по всем записям показывает правильное значение превышения калорий на эту дату
            mapCaloriesPerDay.merge(date, userMeal.getCalories(), Integer::sum);
            if (!mapExcessPerDay.containsKey(date)) mapExcessPerDay.put(date, new AtomicBoolean(false));
            AtomicBoolean excess = mapExcessPerDay.get(date);
            excess.set(mapCaloriesPerDay.get(date) - caloriesPerDay > 0);
            // Отбираем записи по времени, создаём новый объект UserMealWithExcess с полем excess ссылочного типа
            if (TimeUtil.isBetweenHalfOpen(time, startTime, endTime)) {
                result.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), excess));
            }
        }
        return result;
    }

    // Stream - Optional 2
    public static List<UserMealWithExcess> filteredByOneStream(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // Формируем лист на выдачу
        return meals.stream()
                .collect(new ExcessCollector(startTime, endTime, caloriesPerDay));
    }


    // Коллектор для формирования листа UserMealWithExcess, с сортировкой по времени приёма пищи
    public static class ExcessCollector implements Collector<UserMeal, ExcessCollector.Holder, List<UserMealWithExcess>> {

        private static LocalTime startTime;
        private static LocalTime endTime;
        private static int caloriesPerDay;

        public ExcessCollector(LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.caloriesPerDay = caloriesPerDay;
        }

        static class Holder {
            final List<UserMealWithExcess> list;                // Результирующий лист с записями формата UserMealWithExcess
            final Map<LocalDate, Integer> mapCal;               // Карта для подсчёта калорий по дате
            final Map<LocalDate, AtomicBoolean> mapExc;         // Карта для учёта превышения по дате
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
                LocalDate date = meal.getDate();
                holder.mapCal.merge(date, meal.getCalories(), Integer::sum);        // Подсчитываем калории за день
                AtomicBoolean excess;
                if (!holder.mapExc.containsKey(date)) {
                    excess = new AtomicBoolean(false);                    // Создаём ссылочный boolean для учёта превышения калорий, и дальше работаем с сылкой на него
                    holder.mapExc.put(date, excess);                                // Помещаем новую запись в карту учёта превышения калорий
                } else {
                    excess = holder.mapExc.get(date);                               // Берём ссылку на превышение из карты
                }
                if (holder.mapCal.get(date) - caloriesPerDay > 0) {
                    excess.set(true);                                               // Если превышение достигнуто меняем значение по взятой ссылке
                }
                // Если запись подходит по времени на выдачу создаём новую запись UserMealWithExcess и кладём в результирующий лист
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
                return holder1; };
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
