package com.olaleyeone.auth.controller.key;

import com.github.olaleyeone.auth.annotations.Public;
import com.olaleyeone.auth.data.enums.JwtTokenType;
import com.olaleyeone.auth.response.pojo.JsonWebKey;
import com.olaleyeone.auth.repository.SignatureKeyRepository;
import com.github.olaleyeone.rest.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class SignatureKeyController {

    private final SignatureKeyRepository signatureKeyRepository;

    @Public
    @GetMapping("/keys/{kid}")
    public JsonWebKey getJsonWebKey(@PathVariable("kid") String kid) {
        return signatureKeyRepository.findByKeyIdAndType(kid, JwtTokenType.ACCESS)
                .map(signatureKey -> new JsonWebKey(signatureKey)).orElseThrow(NotFoundException::new);
    }
}
