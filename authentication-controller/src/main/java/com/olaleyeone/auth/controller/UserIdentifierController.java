package com.olaleyeone.auth.controller;

import com.github.olaleyeone.auth.annotations.Public;
import com.olaleyeone.auth.constraints.ValidPhoneNumber;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
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
        if (portalUserIdentifierRepository.findActiveByIdentifier(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Public
    @RequestMapping(value = "/user-phone-numbers/{phoneNumber}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> checkPhoneNumberExistence(@PathVariable @ValidPhoneNumber String phoneNumber) {
        if (portalUserIdentifierRepository.findActiveByIdentifier(phoneNumber).isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
