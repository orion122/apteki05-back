package ru.apteki05.utils;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public class Utils {
    private Utils() {
    }

    public static <T, R> List<T> mapList(Collection<R> collection, Function<R, T> mapper) {
        return collection.stream()
                .map(mapper)
                .collect(toList());
    }

    public static <T> List<T> filterList(Collection<T> collection, Predicate<T> filter) {
        return collection.stream()
                .filter(filter)
                .collect(toList());
    }
}
