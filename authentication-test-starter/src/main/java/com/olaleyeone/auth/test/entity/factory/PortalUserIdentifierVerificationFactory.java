package com.olaleyeone.auth.test.entity.factory;

import com.github.heywhy.springentityfactory.contracts.FactoryHelper;
import com.github.heywhy.springentityfactory.contracts.ModelFactory;
import com.github.javafaker.Faker;
import com.olaleyeone.auth.data.entity.PortalUserIdentifierVerification;
import com.olaleyeone.auth.data.enums.UserIdentifierType;

import java.time.LocalDateTime;

public class PortalUserIdentifierVerificationFactory implements FactoryHelper<PortalUserIdentifierVerification> {

    @Override
    public Class<PortalUserIdentifierVerification> getEntity() {
        return PortalUserIdentifierVerification.class;
    }

    @Override
    public PortalUserIdentifierVerification apply(Faker faker, ModelFactory factory) {
        PortalUserIdentifierVerification verification = new PortalUserIdentifierVerification();
        verification.setIdentifier(faker.internet().emailAddress());
        verification.setIdentifierType(UserIdentifierType.EMAIL);
        verification.setVerificationCodeHash(faker.internet().emailAddress());
        verification.setExpiresOn(LocalDateTime.now().plusMinutes(1));
        return verification;
    }
}
