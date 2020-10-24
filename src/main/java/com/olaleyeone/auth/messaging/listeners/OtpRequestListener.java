package com.olaleyeone.auth.messaging.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.messaging.message.OtpRequestMessage;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.auth.service.OneTimePasswordService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Builder
@Component
public class OtpRequestListener {

    final Logger logger = LoggerFactory.getLogger(getClass());

    private final TaskContextFactory taskContextFactory;
    private final ObjectMapper objectMapper;
    private final PortalUserIdentifierRepository portalUserIdentifierRepository;
    private final OneTimePasswordService oneTimePasswordService;

    @SneakyThrows
    @KafkaListener(topics = "${otp_request.topic.name}", groupId = "${kafka.groupId}")
    public void listen(String message) {
        logger.info("{}", message);
        taskContextFactory.startBackgroundTask(
                "PROCESS OTP REQUEST",
                "Start background job to process OTP request",
                () -> startTask(message));
    }

    @SneakyThrows
    private void startTask(String message) {
        OtpRequestMessage otpRequestMessage = objectMapper.readValue(message, OtpRequestMessage.class);
        portalUserIdentifierRepository.findActiveByIdentifier(otpRequestMessage.getIdentifier())
                .ifPresent(portalUserIdentifier -> oneTimePasswordService.createOTP(portalUserIdentifier, otpRequestMessage.getPassword()));
    }
}
