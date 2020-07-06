package com.github.olaleyeone.converter;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

/**
 * @author Olaleye Afolabi <oafolabi@byteworks.com.ng>
 */
public final class DateConverter implements Converter<String, Date> {

    private final String pattern;

    public DateConverter(String dateFormat) {
        this.pattern = dateFormat;
    }

    @SneakyThrows
    @Override
    public Date convert(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        return DateUtils.parseDate(source, pattern);
    }
}
