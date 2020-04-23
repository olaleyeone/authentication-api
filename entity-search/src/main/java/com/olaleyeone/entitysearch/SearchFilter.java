package com.olaleyeone.entitysearch;

import com.querydsl.core.types.EntityPath;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;

public interface SearchFilter<E, Q extends EntityPath<E>> extends QuerydslBinderCustomizer<Q> {
}
