package com.github.olaleyeone.entitysearch;

import com.querydsl.core.QueryResults;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchResultApiResponseTest {

    @Test
    public void test() {
        QueryResults<Integer> queryResults = new QueryResults<Integer>(Arrays.asList(1, 2, 3, 4, 5), 5L, 10L, Integer.MAX_VALUE);
        List<String> list = Arrays.asList("a", "b", "c");
        SearchResultApiResponse<String> resultApiResponse = new SearchResultApiResponse<>(list, queryResults);
        assertEquals(queryResults.getLimit(), resultApiResponse.getLimit());
        assertEquals(queryResults.getOffset(), resultApiResponse.getOffset());
        assertEquals(queryResults.getTotal(), resultApiResponse.getTotal());
        assertEquals(list, resultApiResponse.getResults());
    }
}