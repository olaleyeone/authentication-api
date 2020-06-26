package com.olaleyeone.auth.data.dto;

import com.olaleyeone.auth.data.enums.Gender;
import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.constraints.UniqueIdentifier;
import com.olaleyeone.auth.constraints.ValidEmailVerificationCode;
import com.olaleyeone.auth.constraints.ValidPhoneNumber;
import lombok.Data;

import javax.validation.constraints.Email;
import java.util.List;

@Data
@ValidEmailVerificationCode
public class UserRegistrationApiRequest {

    private String password;

    private String displayName;

    private String firstName;

    private String lastName;

    private String otherName;

    @ValidPhoneNumber
    @UniqueIdentifier(UserIdentifierType.PHONE_NUMBER)
    private String phoneNumber;
    private String phoneNumberVerificationCode;

    @Email
    @UniqueIdentifier(UserIdentifierType.EMAIL)
    private String email;

    private String emailVerificationCode;

    private Gender gender;

    private List<UserDataApiRequest> data;
}
