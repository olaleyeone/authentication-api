package com.olaleyeone.auth.security.access;

import com.olaleyeone.auth.security.annotations.TrustedIpAddress;

public interface TrustedIpAddressAuthorizer {

    AccessStatus getStatus(TrustedIpAddress accessConstraint, String ipAddress);
}
