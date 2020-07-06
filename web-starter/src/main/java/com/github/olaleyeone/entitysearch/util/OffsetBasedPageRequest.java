package com.github.olaleyeone.entitysearch.util;

import lombok.Data;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

@Data
public class OffsetBasedPageRequest implements Pageable, Serializable {

    private static final long serialVersionUID = -25822477129613575L;

    private final int limit;
    private final long offset;
    private final Sort sort;

    /**
     * Creates a new {@link com.github.olaleyeone.entitysearch.util.OffsetBasedPageRequest} with sort parameters applied.
     *
     * @param offset zero-based offset.
     * @param limit  the size of the elements to be returned.
     * @param sort   can be {@literal null}.
     */
    public OffsetBasedPageRequest(long offset, int limit, Sort sort) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset index must not be less than zero!");
        }

        if (limit < 1) {
            throw new IllegalArgumentException("Limit must not be less than one!");
        }
        this.limit = limit;
        this.offset = offset;
        this.sort = sort;
    }

    @Override
    public int getPageNumber() {
        return Long.valueOf(offset / limit).intValue();
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new OffsetBasedPageRequest(getOffset() + getPageSize(), getPageSize(), getSort());
    }

    @Override
    public Pageable first() {
        return new OffsetBasedPageRequest(0L, getPageSize(), getSort());
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    public boolean hasPrevious() {
        return offset >= limit;
    }

    private OffsetBasedPageRequest previous() {
        return new OffsetBasedPageRequest(getOffset() - getPageSize(), getPageSize(), getSort());
    }
}

