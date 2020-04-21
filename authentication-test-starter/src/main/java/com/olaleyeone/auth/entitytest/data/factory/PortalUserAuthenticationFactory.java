package com.olaleyeone.auth.entitytest.data.factory;

import com.github.heywhy.springentityfactory.contracts.FactoryHelper;
import com.github.heywhy.springentityfactory.contracts.ModelFactory;
import com.github.javafaker.Faker;
import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.data.enums.AuthenticationType;

public class PortalUserAuthenticationFactory implements FactoryHelper<PortalUserAuthentication> {

    @Override
    public Class<PortalUserAuthentication> getEntity() {
        return PortalUserAuthentication.class;
    }

    @Override
    public PortalUserAuthentication apply(Faker faker, ModelFactory factory) {
        PortalUserIdentifier portalUserIdentifier = factory.create(PortalUserIdentifier.class);

        PortalUserAuthentication portalUserAuthentication = new PortalUserAuthentication();
        portalUserAuthentication.setPortalUserIdentifier(portalUserIdentifier);
        portalUserAuthentication.setIdentifier(portalUserIdentifier.getIdentifier());
        portalUserAuthentication.setType(AuthenticationType.LOGIN);
        portalUserAuthentication.setResponseType(AuthenticationResponseType.SUCCESSFUL);
        portalUserAuthentication.setIpAddress(faker.internet().ipV4Address());
        portalUserAuthentication.setUserAgent(faker.internet().userAgentAny());
        return portalUserAuthentication;
    }
}
