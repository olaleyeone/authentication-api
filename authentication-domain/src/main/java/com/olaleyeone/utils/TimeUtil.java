package com.olaleyeone.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Optional;

public class TimeUtil {

    private TimeUtil() {
        //noop
    }

    public static Instant toInstant(LocalDateTime expiresOn) {
        return Optional.ofNullable(expiresOn)
                .map(it -> it.atZone(ZoneId.systemDefault()).toInstant())
                .orElse(null);
    }

    public static Long secondsTill(Temporal instant) {
        return Instant.now().until(instant, ChronoUnit.SECONDS);
    }
}
