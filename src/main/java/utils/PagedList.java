package utils;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PagedList<T> {
    private final List<T> items;
    @Getter
    private final int page;
    @Getter
    private final int pageSize;
    @Getter
    private final long totalCount;

    public PagedList(List<T> items, int page, int pageSize, long totalCount) {
        this.items = new ArrayList<>(items);
        this.page = page;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
    }

    public List<T> getPageItems() {
        int fromIndex = Math.min(page * pageSize, items.size());
        int toIndex = Math.min(fromIndex + pageSize, items.size());
        return items.subList(fromIndex, toIndex);
    }

    public int totalPages() {
        return (int) Math.ceil((double) totalCount / pageSize);
    }
}
