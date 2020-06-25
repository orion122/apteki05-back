package ru.apteki05.output;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

@Data
@AllArgsConstructor
public class ListResultUtil<T, R> {
    private List<T> items;
    private long size;
    private long page;
    private Function<T, R> function;

    public static <T, R> ListResult<R> of(List<T> items, Integer size, Integer page, Function<T, R> function) {
        List<R> limitedItems = items.stream()
                .skip(page * size)
                .limit(size)
                .map(function)
                .collect(toList());

        return ListResult.of(limitedItems, items.size(), size, page);
    }
}
