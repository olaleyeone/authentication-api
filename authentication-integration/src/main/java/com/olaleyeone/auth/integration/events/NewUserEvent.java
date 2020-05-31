package com.olaleyeone.auth.integration.events;

import com.olaleyeone.auth.data.entity.PortalUser;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class NewUserEvent {

    private final PortalUser portalUser;
}
