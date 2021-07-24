package ru.javawebinar.topjava.util;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalTime;

public class StringToLocalTimeConverter implements Converter<String, LocalTime> {
    public LocalTime convert(String source) {
        return LocalTime.parse(source);
    }
}
