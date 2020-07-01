package com.github.olaleyeone.configuration;

import com.github.olaleyeone.entitysearch.util.PredicateExtractor;
import com.github.olaleyeone.entitysearch.util.SearchFilterPredicateExtractor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PredicateConfiguration {

    @Bean
    public PredicateExtractor predicateExtractor(ApplicationContext applicationContext) {
        return applicationContext.getAutowireCapableBeanFactory().createBean(PredicateExtractor.class);
    }

    @Bean
    public SearchFilterPredicateExtractor searchFilterPredicateExtractor(PredicateExtractor predicateExtractor) {
        return new SearchFilterPredicateExtractor(predicateExtractor);
    }
}
