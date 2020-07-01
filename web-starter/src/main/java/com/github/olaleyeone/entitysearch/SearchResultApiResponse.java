package com.github.olaleyeone.entitysearch;

import com.querydsl.core.QueryResults;
import lombok.Data;

import java.util.List;

@Data
public class SearchResultApiResponse<T> {

    private final long limit, offset, total;

    private final List<T> results;

    public SearchResultApiResponse(List<T> results, QueryResults<?> queryResults) {
        this.results = results;
        this.limit = queryResults.getLimit();
        this.offset = queryResults.getOffset();
        this.total = queryResults.getTotal();
    }
}