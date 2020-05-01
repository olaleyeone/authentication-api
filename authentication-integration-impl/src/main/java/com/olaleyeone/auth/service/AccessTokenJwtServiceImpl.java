package com.olaleyeone.auth.service;

import com.olaleyeone.audittrail.embeddable.Duration;
import com.olaleyeone.audittrail.entity.Task;
import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.audittrail.impl.TaskContextImpl;
import com.olaleyeone.audittrail.impl.TaskContextSaver;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.dto.JwtDto;
import com.olaleyeone.auth.qualifier.JwtToken;
import com.olaleyeone.auth.qualifier.JwtTokenType;
import com.olaleyeone.auth.security.data.AccessClaims;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.time.Instant;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Builder
@Named
@JwtToken(JwtTokenType.ACCESS)
public class AccessTokenJwtServiceImpl implements JwtService {

    private final KeyGenerator keyGenerator;
    private final TaskContextFactory taskContextFactory;
    private final TaskContextSaver taskContextSaver;
    private final BaseJwtService baseJwtService;

    @PostConstruct
    public void init() {
        Task task = new Task();
        task.setType(Task.BACKGROUND_JOB);
        task.setName("INITIALIZE ACCESS TOKEN KEY");
        task.setDuration(Duration.builder()
                .startedOn(LocalDateTime.now())
                .build());
        TaskContextImpl taskContext = taskContextFactory.start(task);
        baseJwtService.updateKey(keyGenerator.generateKey());
        taskContextSaver.save(taskContext);
    }

    @Override
    public JwtDto generateJwt(RefreshToken refreshToken) {
        JwtDto jwtDto = new JwtDto();
        jwtDto.setSecondsTillExpiry(refreshToken.getSecondsTillAccessExpiry());
        jwtDto.setToken(baseJwtService.createJwt(refreshToken, refreshToken.getAccessExpiryInstant()));
        return jwtDto;
    }

    @Override
    public AccessClaims parseAccessToken(String jws) {
        return baseJwtService.parseAccessToken(jws);
    }

}
