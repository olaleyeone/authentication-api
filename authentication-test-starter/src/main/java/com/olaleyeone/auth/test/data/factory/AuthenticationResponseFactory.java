package com.olaleyeone.auth.test.data.factory;

import com.github.heywhy.springentityfactory.contracts.FactoryHelper;
import com.github.heywhy.springentityfactory.contracts.ModelFactory;
import com.github.javafaker.Faker;
import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.data.enums.AuthenticationType;

public class AuthenticationResponseFactory implements FactoryHelper<PortalUserAuthentication> {

    @Override
    public Class<PortalUserAuthentication> getEntity() {
        return PortalUserAuthentication.class;
    }

    @Override
    public PortalUserAuthentication apply(Faker faker, ModelFactory factory) {
        PortalUserAuthentication authenticationResponse = new PortalUserAuthentication();
        authenticationResponse.setPortalUserIdentifier(factory.create(PortalUserIdentifier.class));
        authenticationResponse.setIdentifier(faker.internet().emailAddress());
        authenticationResponse.setType(AuthenticationType.LOGIN);
        authenticationResponse.setResponseType(AuthenticationResponseType.SUCCESSFUL);
        authenticationResponse.setIpAddress(faker.internet().ipV4Address());
        authenticationResponse.setUserAgent(faker.internet().userAgentAny());
        return authenticationResponse;
    }
}
