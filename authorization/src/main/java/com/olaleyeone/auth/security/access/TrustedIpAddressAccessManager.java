package com.olaleyeone.auth.security.access;

import com.olaleyeone.auth.security.annotations.TrustedIpAddress;

public interface TrustedIpAddressAccessManager {

    AccessStatus getStatus(TrustedIpAddress accessConstraint, String ipAddress);
}
