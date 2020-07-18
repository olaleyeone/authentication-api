package com.olaleyeone.auth.integration.events;

import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class SessionUpdateEvent {

    private final PortalUserAuthentication userAuthentication;
}
