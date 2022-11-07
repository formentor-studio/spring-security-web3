package org.formentor.web3.server;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.formentor.web3.server.service.InvalidSignatureException;
import org.formentor.web3.server.service.JWTService;
import org.formentor.web3.server.service.NFTService;
import org.formentor.web3.server.model.Signature;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/*
{
    "access_token": "eyJraWQiOiI0YzFhNjZhMy05MmM4LTQ2MDQtOWU3ZC04NTkwNDVmMjQxNGQiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJtZXNzYWdpbmctY2xpZW50IiwiYXVkIjoibWVzc2FnaW5nLWNsaWVudCIsIm5iZiI6MTY2Njk2OTAxNSwic2NvcGUiOlsibWVzc2FnZTpyZWFkIl0sImlzcyI6Imh0dHA6XC9cL2xvY2FsaG9zdDo5MDAwIiwiZXhwIjoxNjY2OTY5MzE1LCJpYXQiOjE2NjY5NjkwMTV9.XkXNjkFhXp3eIjMcNsMuMdONbAk8aczKP75h8FdOmqS-ZAQEYRR--ocjyLAer_8qlWFEKxNWvuqbfrIRbyLBg7xCFynSi3qr81SiVTnZ7OAH1xwc4ZxJVeIaPXcVAt6sHzZ3i0gcyNYNtpvI9b43g8ioxrWK0lWLFgvUoQsjuUohDWmDbE-3h0t1SVDQfrmwcQadu9sSjFarKvu3OQIJfQ-yIg6du3M-GFt-fKxElI5xwy-9FmeT7MtDQC04gDGM2PIIRZH53wnVaVUQ6URm-qOu2Kp4IV1yT32g9GNtmh1j2_PUwzeP4KHz8CACkEYmD8Q3o_eTCrhUUAsYReD1iA",
    "scope": "message:read",
    "token_type": "Bearer",
    "expires_in": 299
}
*/

public class JWTServiceTest {

    private final static Signature SAMPLE_SIGNATURE_VALID = Signature.builder()
            .identity("0x44685669260d54e20aece43bfb7fd9f47502d28e")
            .message("Random message")
            .v("0x1c")
            .r("0xabbf8f23e5aa8125f5d711ab5ad94e8ebc61b3ddb25d4978c72f687cb7181537")
            .s("0x2fac3ddfa89aa503df59b7afc97051106148d075abf36507df31137cc897cf34")
            .build();

    @Test
    void buildJWT_ShouldSpecifyIdentityAsSubject() {
        final JWTService service = new JWTService(Optional::empty, mock(NFTService.class));

        final String token = service.buildJWT(SAMPLE_SIGNATURE_VALID);

        assertEquals(SAMPLE_SIGNATURE_VALID.getIdentity(), JWT.decode(token).getSubject());
    }

    @Test
    void buildJWT_IdentitySignatureInvalidShouldThrowException() {
        final Signature signature = Signature.builder()
                .identity("0x44685669260d54e20aece43bfb7fd9f47502d28e")
                .message("Invalid signature")
                .v("0x1c")
                .r("0xabbf8f23e5aa8125f5d711ab5ad94e8ebc61b3ddb25d4978c72f687cb7181537")
                .s("0x2fac3ddfa89aa503df59b7afc97051106148d075abf36507df31137cc897cf34")
                .build();
        final JWTService service = new JWTService(Optional::empty, mock(NFTService.class));

        assertThrows(InvalidSignatureException.class, () -> service.buildJWT(signature)) ;
    }


    @Test
    void buildJWT_ShouldBeSigned() {
        KeyPair rsaKeyPair = generateRsaKey();
        // Just to document how to derive the public and private key to String
        String pubKey = Base64.getEncoder().encodeToString(rsaKeyPair.getPublic().getEncoded());
        String prvKey = Base64.getEncoder().encodeToString(rsaKeyPair.getPrivate().getEncoded());
        System.out.printf("-----BEGIN PUBLIC KEY-----%n%s%n-----END PUBLIC KEY-----%n", pubKey);
        System.out.println();
        System.out.printf("-----BEGIN PRIVATE KEY-----%n%s%n-----END PRIVATE KEY-----%n", prvKey);

        final JWTService service = new JWTService(() -> Optional.of((RSAKey)rsaKeyPair.getPrivate()), mock(NFTService.class));

        final String token = service.buildJWT(SAMPLE_SIGNATURE_VALID);

        JWTVerifier jwtVerifier = JWT.require(Algorithm.RSA256((RSAPublicKey)rsaKeyPair.getPublic())).build();
        jwtVerifier.verify(token);
    }

    @Test
    void buildJWT_ShouldSpecifyScope() {
        NFTService nftService = mock(NFTService.class);
        final String[] scope = new String[]{"Level-A", "Level-B"};
        when(nftService.ownedBy(anyString())).thenReturn(scope);
        final JWTService service = new JWTService(Optional::empty, nftService);

        final String token = service.buildJWT(SAMPLE_SIGNATURE_VALID);

        assertArrayEquals(scope, JWT.decode(token).getClaim("scope").asArray(String.class));
    }

    private static KeyPair generateRsaKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

}
