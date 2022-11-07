package org.formentor.web3.server;

import org.formentor.web3.server.controller.AuthorizationServerController;
import org.formentor.web3.server.service.JWTService;
import org.formentor.web3.server.model.Signature;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

public class AuthorizationServerControllerTest {

    @Test
    void token_ShouldBuildJWT() {
        JWTService authorizationService = mock(JWTService.class);
        AuthorizationServerController controller = new AuthorizationServerController(authorizationService);
        final Signature signature = Signature.builder().identity("0x").message("does-not-mind").build();

        controller.tokenByRawBody(signature);

        Mockito.verify(authorizationService).buildJWT(signature);
    }

    @Test
    void token_SignatureAsMapShouldBuildJWT() {
        JWTService authorizationService = mock(JWTService.class);
        AuthorizationServerController controller = new AuthorizationServerController(authorizationService);
        final Map<String, String> signatureAsMap = Map.of(
                "identity", "0x44685669260d54e20aece43bfb7fd9f47502d28e",
                "message", "Random message",
                "v", "0x1c",
                "r", "0xabbf8f23e5aa8125f5d711ab5ad94e8ebc61b3ddb25d4978c72f687cb7181537",
                "s", "0x2fac3ddfa89aa503df59b7afc97051106148d075abf36507df31137cc897cf34"
        );

        controller.tokenByForm(signatureAsMap);

        Mockito.verify(authorizationService).buildJWT(any(Signature.class));
    }
}
