package com.olaleyeone.auth.dto.data;

import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.dto.constraints.UniqueIdentifier;
import com.olaleyeone.auth.dto.constraints.ValidPhoneNumber;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserRegistrationDto {

    @NotBlank
    private String password;

    @NotBlank
    private String firstName;

    private String lastName;

    private String otherName;

    @NotBlank
    @ValidPhoneNumber
    @UniqueIdentifier(UserIdentifierType.PHONE_NUMBER)
    private String phoneNumber;
    private String phoneNumberVerificationCode;

    @NotBlank
    @Email
    @UniqueIdentifier(UserIdentifierType.EMAIL)
    private String email;
    private String emailVerificationCode;
}
