package com.olaleyeone.auth.test.entity.factory;

import com.github.heywhy.springentityfactory.contracts.FactoryHelper;
import com.github.heywhy.springentityfactory.contracts.ModelFactory;
import com.github.javafaker.Faker;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;

import java.time.OffsetDateTime;

public class PasswordResetRequestFactory implements FactoryHelper<PasswordResetRequest> {

    @Override
    public Class<PasswordResetRequest> getEntity() {
        return PasswordResetRequest.class;
    }

    @Override
    public PasswordResetRequest apply(Faker faker, ModelFactory factory) {
        PasswordResetRequest passwordResetRequest = new PasswordResetRequest();
        passwordResetRequest.setPortalUserIdentifier(factory.create(PortalUserIdentifier.class));
        passwordResetRequest.setResetCode(faker.internet().password());
        passwordResetRequest.setResetCodeHash(faker.internet().password());
        passwordResetRequest.setExpiresAt(OffsetDateTime.now().plusDays(2));
        passwordResetRequest.setIpAddress(faker.internet().ipV4Address());
        passwordResetRequest.setUserAgent(faker.internet().userAgentAny());
        return passwordResetRequest;
    }
}
