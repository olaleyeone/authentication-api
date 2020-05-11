package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserData;
import com.olaleyeone.auth.dto.data.UserDataApiRequest;

public interface PortalUserDataService {

    PortalUserData addData(PortalUser portalUser, UserDataApiRequest entry);
}
