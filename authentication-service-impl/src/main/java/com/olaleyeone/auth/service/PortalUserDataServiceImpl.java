package com.olaleyeone.auth.service;

import com.olaleyeone.audittrail.api.Activity;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserData;
import com.olaleyeone.auth.data.dto.UserDataApiRequest;
import com.olaleyeone.auth.repository.PortalUserDataRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Named;
import javax.transaction.Transactional;

@RequiredArgsConstructor
@Named
public class PortalUserDataServiceImpl implements PortalUserDataService {

    private final PortalUserDataRepository portalUserDataRepository;

    @Activity("ADD USER DATA")
    @Transactional
    @Override
    public PortalUserData addData(PortalUser portalUser, UserDataApiRequest entry) {
        PortalUserData data = new PortalUserData();
        data.setPortalUser(portalUser);
        data.setName(entry.getName());
        data.setValue(entry.getValue());
        portalUserDataRepository.save(data);
        return data;
    }
}
