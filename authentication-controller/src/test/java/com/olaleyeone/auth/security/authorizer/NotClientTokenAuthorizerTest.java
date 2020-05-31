package com.olaleyeone.auth.security.authorizer;

import com.github.olaleyeone.auth.data.AccessClaims;
import com.olaleyeone.auth.test.ComponentTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotClientTokenAuthorizerTest extends ComponentTest {

    @Mock
    private AccessClaims accessClaims;

    @Spy
    private NotClientTokenAuthorizer authorizer;

    @Test
    void getStatusForNonClientUser() {
        Mockito.doReturn(Collections.EMPTY_LIST).when(accessClaims).getAudience();
        assertTrue(authorizer.getStatus(null, accessClaims).hasAccess());
    }

    @Test
    void getStatusForClientUser() {
        Mockito.doReturn(Collections.singletonList("")).when(accessClaims).getAudience();
        assertFalse(authorizer.getStatus(null, accessClaims).hasAccess());
    }
}