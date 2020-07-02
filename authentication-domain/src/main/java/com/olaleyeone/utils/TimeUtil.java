package com.olaleyeone.utils;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Optional;

public final class TimeUtil {

    private TimeUtil() {
        //noop
    }

    public static Instant toInstant(OffsetDateTime expiresOn) {
        return Optional.ofNullable(expiresOn)
                .map(it -> it.toInstant())
                .orElse(null);
    }

    public static Long secondsTill(Temporal instant) {
        return Instant.now().until(instant, ChronoUnit.SECONDS);
    }
}
