package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.auth.security.annotations.Public;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Email;

@RequiredArgsConstructor
@Validated
@RestController
public class UserIdentifierController {

    private final PortalUserIdentifierRepository portalUserIdentifierRepository;

    @Public
    @RequestMapping(value = "/user-emails/{email}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> checkEmailExistence(@PathVariable @Email String email) {
        if (portalUserIdentifierRepository.findByIdentifier(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
