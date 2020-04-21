package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.security.constraint.NotClientToken;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PasswordUpdateController {

    @NotClientToken
    @PostMapping("/password")
    public void changePassword() {

    }
}
