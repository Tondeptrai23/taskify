package com.taskify.commoncore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ApiCollectionResponse<T> {
    private boolean success;
    private List<T> content;
    private Pagination pagination;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Pagination {
        private int pageNumber;
        private int pageSize;
        private long totalElements;
        private int totalPages;
        private boolean isLast;
    }

    public static <T> ApiCollectionResponse<T> from(Page<T> page) {
        return new ApiCollectionResponse<>(
                true,
                page.getContent(),
                new Pagination(
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages(),
                        page.isLast()
                )
        );
    }
}
