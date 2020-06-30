package ru.apteki05.output;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
@AllArgsConstructor
public class ListResultUtil {

    public static <T> ListResult<T> getResult(List<T> items, Integer page, Integer size) {
        List<T> limitedItems = items.stream()
                .skip((page - 1) * size)
                .limit(size)
                .collect(toList());

        return ListResult.of(limitedItems, items.size(), page, size);
    }
}
