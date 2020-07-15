package com.olaleyeone.auth.service;

import com.olaleyeone.audittrail.api.Activity;
import com.olaleyeone.auth.data.dto.UserDataApiRequest;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthenticationData;
import com.olaleyeone.auth.repository.PortalUserAuthenticationDataRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Named;
import javax.transaction.Transactional;

@RequiredArgsConstructor
@Named
public class PortalUserAuthenticationDataServiceImpl implements PortalUserAuthenticationDataService{

    private final PortalUserAuthenticationDataRepository portalUserAuthenticationDataRepository;

    @Activity("ADD AUTHENTICATION DATA")
    @Transactional
    @Override
    public PortalUserAuthenticationData addData(PortalUserAuthentication userAuthentication, UserDataApiRequest entry) {
        PortalUserAuthenticationData data = new PortalUserAuthenticationData();
        data.setPortalUserAuthentication(userAuthentication);
        data.setName(entry.getName());
        data.setValue(entry.getValue());
        portalUserAuthenticationDataRepository.save(data);
        return data;
    }
}
