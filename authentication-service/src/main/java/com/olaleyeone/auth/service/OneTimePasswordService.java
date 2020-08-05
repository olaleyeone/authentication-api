package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.OneTimePassword;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;

import java.util.Map;

public interface OneTimePasswordService {

    Map.Entry<OneTimePassword, String> createOTP(PortalUserIdentifier identifier);
}
