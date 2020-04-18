package com.olaleyeone.auth.test.data.factory;

import com.github.heywhy.springentityfactory.contracts.FactoryHelper;
import com.github.heywhy.springentityfactory.contracts.ModelFactory;
import com.github.javafaker.Faker;
import com.olaleyeone.auth.data.entity.AuthenticationResponse;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;

public class AuthenticationResponseFactory implements FactoryHelper<AuthenticationResponse> {

    @Override
    public Class<AuthenticationResponse> getEntity() {
        return AuthenticationResponse.class;
    }

    @Override
    public AuthenticationResponse apply(Faker faker, ModelFactory factory) {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setPortalUserIdentifier(factory.create(PortalUserIdentifier.class));
        authenticationResponse.setIdentifier(faker.internet().emailAddress());
        authenticationResponse.setResponseType(AuthenticationResponseType.SUCCESSFUL);
        authenticationResponse.setIpAddress(faker.internet().ipV4Address());
        authenticationResponse.setUserAgent(faker.internet().userAgentAny());
        return authenticationResponse;
    }
}
