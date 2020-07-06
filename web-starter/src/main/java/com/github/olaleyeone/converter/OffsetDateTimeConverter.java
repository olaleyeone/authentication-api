package com.github.olaleyeone.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Olaleye Afolabi <oafolabi@byteworks.com.ng>
 */
public final class OffsetDateTimeConverter implements Converter<String, OffsetDateTime> {

    private final DateTimeFormatter formatter;

    public OffsetDateTimeConverter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public OffsetDateTime convert(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }

        return OffsetDateTime.parse(source, formatter);
    }
}
