package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.SignatureKey;
import com.olaleyeone.auth.repository.SignatureKeyRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Scope(DefaultListableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@Named
class SigningKeyResolverImpl extends SigningKeyResolverAdapter {

    private final SignatureKeyRepository signatureKeyRepository;

    @Value("${MAX_KEY_CACHE_SIZE:25}")
    @Setter
    @Getter
    private Integer maxSize = 25;

    private final Lock lock = new ReentrantLock();
    private Map<String, Key> keyMap = new HashMap<>();
    private TreeSet<Map.Entry<String, LocalDateTime>> set = new TreeSet<>(Comparator.comparing(Map.Entry::getValue));

    public void registerKey(SignatureKey signatureKey) {
        addKey(signatureKey);
    }

    private void addKey(SignatureKey signatureKey) {
        try {
            lock.lock();
            if (set.size() >= maxSize && signatureKey.getCreatedOn().isBefore(set.first().getValue())) {
                return;
            }
            keyMap.put(signatureKey.getKeyId(), signatureKey.getRsaPublicKey());
            set.add(Pair.of(signatureKey.getKeyId(), signatureKey.getCreatedOn()));

            while (set.size() > maxSize) {
                Iterator<Map.Entry<String, LocalDateTime>> iterator = set.iterator();
                Map.Entry<String, LocalDateTime> next = iterator.next();
                iterator.remove();
                keyMap.remove(next.getKey());
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Key resolveSigningKey(JwsHeader header, Claims claims) {
        Key key = keyMap.get(header.getKeyId());
        if (key != null) {
            return key;
        }
        return signatureKeyRepository.findByKeyId(header.getKeyId())
                .map(signatureKey -> {
                    addKey(signatureKey);
                    return signatureKey.getRsaPublicKey();
                }).orElse(null);
    }
}
