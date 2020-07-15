package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.dto.UserDataApiRequest;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthenticationData;

public interface PortalUserAuthenticationDataService {

    PortalUserAuthenticationData addData(PortalUserAuthentication userAuthentication, UserDataApiRequest entry);
}
