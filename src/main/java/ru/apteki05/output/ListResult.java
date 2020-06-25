package ru.apteki05.output;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListResult<T> {
    private List<T> items;
    private long total;
    private long page;
    private long size;

    public static <T> ListResult<T> of(List<T> items, Integer total, Integer page, Integer size) {
        return new ListResult<T>(items, total, page, size);
    }
}
