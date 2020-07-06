package com.github.olaleyeone.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author Olaleye Afolabi <oafolabi@byteworks.com.ng>
 */
public final class LocalDateConverter implements Converter<String, LocalDate> {

    private final DateTimeFormatter formatter;

    public LocalDateConverter(String dateFormat) {
        this.formatter = DateTimeFormatter.ofPattern(dateFormat);
    }

    @Override
    public LocalDate convert(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }

        return LocalDate.parse(source, formatter);
    }
}
