package com.olaleyeone.auth.data.embeddable;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.PrePersist;
import java.time.LocalDateTime;

@Embeddable
@Getter
public class PersistTimeSetter {

    @Column(updatable = false, nullable = false)
    private LocalDateTime dateCreated;

    @PrePersist
    public void prePersist() {
        dateCreated = LocalDateTime.now();
    }
}
