package org.formentor.web3.server.controller;

import org.formentor.web3.server.service.JWTService;
import org.formentor.web3.server.service.NFTService;
import org.formentor.web3.server.model.Signature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class AuthorizationServerController {
    private final JWTService authorizationService;

    @Autowired
    private NFTService nftService;

    public AuthorizationServerController(JWTService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @GetMapping("/nft")
    public List<String> getNft() {
        return nftService.getNFTs();
    }

    @PostMapping(path = "/token", consumes = "application/json")
    public String tokenByRawBody(@RequestBody Signature signature) {
        return authorizationService.buildJWT(signature);
    }

    @PostMapping(path = "/token", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<Void> tokenByForm(@RequestParam Map<String, String> signatureAsMap) { // @RequestParam MultiValueMap
        Signature signature = Signature.builder()
                .identity(signatureAsMap.get("identity"))
                .message(signatureAsMap.get("message"))
                .v(signatureAsMap.get("v"))
                .r(signatureAsMap.get("r"))
                .s(signatureAsMap.get("s"))
                .build();
        String callback = Optional.ofNullable(signatureAsMap.get("callback")).orElse("http://localhost:9080/authorize");
        final String token = authorizationService.buildJWT(signature);

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(callback + "?token=" + token)).build();
    }

}
