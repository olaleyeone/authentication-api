package com.olaleyeone.auth.search;

import lombok.*;

import java.util.Optional;

@Setter
public class PageDto {

    private Integer offset;
    private Integer limit;

    public Optional<Integer> getOffset() {
        return Optional.ofNullable(offset);
    }

    public Optional<Integer> getLimit() {
        return Optional.ofNullable(limit);
    }
}
