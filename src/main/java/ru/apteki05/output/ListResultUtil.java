package ru.apteki05.output;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

@Data
@AllArgsConstructor
public class ListResultUtil<T, R> {

    public static <T, R> ListResult<R> getResult(List<T> items, Integer page, Integer size, Function<T, R> converter) {
        List<R> limitedItems = items.stream()
                .skip(page * size)
                .limit(size)
                .map(converter)
                .collect(toList());

        return ListResult.of(limitedItems, items.size(), page, size);
    }
}
