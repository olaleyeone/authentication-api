package com.olaleyeone.auth.integration.security;

import com.olaleyeone.auth.data.entity.SignatureKey;
import com.olaleyeone.auth.data.enums.JwtTokenType;
import com.olaleyeone.auth.repository.SignatureKeyRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Comparator.comparing;

@RequiredArgsConstructor
public class SimpleSigningKeyResolver extends SigningKeyResolverAdapter {

    private final SignatureKeyRepository signatureKeyRepository;
    private final JwtTokenType jwtTokenType;

    @Setter
    @Getter
    private Integer maxSize = 25;

    private final Lock lock = new ReentrantLock();
    private final Map<String, Key> keyMap = new HashMap<>();
    private final TreeSet<Map.Entry<String, LocalDateTime>> set = new TreeSet<>(comparing(Map.Entry::getValue));

    public void addKey(SignatureKey signatureKey) {
        try {
            lock.lock();
            if (set.size() >= maxSize && signatureKey.getCreatedAt().isBefore(set.first().getValue())) {
                return;
            }
            keyMap.put(signatureKey.getKeyId(), signatureKey.getRsaPublicKey());
            set.add(Pair.of(signatureKey.getKeyId(), signatureKey.getCreatedAt()));

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
        return signatureKeyRepository.findByKeyIdAndType(header.getKeyId(), jwtTokenType)
                .map(signatureKey -> {
                    addKey(signatureKey);
                    return signatureKey.getRsaPublicKey();
                }).orElse(null);
    }
}
