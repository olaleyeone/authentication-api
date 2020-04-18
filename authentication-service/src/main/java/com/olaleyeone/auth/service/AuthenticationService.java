package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.AuthenticationResponse;
import com.olaleyeone.auth.dto.LoginRequestDto;
import com.olaleyeone.auth.dto.RequestMetadata;

public interface AuthenticationService {

    AuthenticationResponse getAuthenticationResponse(LoginRequestDto requestDto, RequestMetadata requestMetadata);
}
