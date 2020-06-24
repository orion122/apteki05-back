package ru.apteki05.output;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListResult<T> {
    private List<T> items;
    private long total;
    private long size;
    private long page;

    public static <T> ListResult<T> of(List<T> items, Integer size, Integer page) {
        return new ListResult<T>(items, items.size(), size, page);
    }
}
