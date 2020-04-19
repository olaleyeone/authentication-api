package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.AuthenticationResponse;
import com.olaleyeone.auth.dto.data.LoginRequestDto;
import com.olaleyeone.auth.dto.data.RequestMetadata;

public interface AuthenticationService {

    AuthenticationResponse getAuthenticationResponse(LoginRequestDto requestDto, RequestMetadata requestMetadata);
}
