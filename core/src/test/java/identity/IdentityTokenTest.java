package identity;

import org.formentor.web3.security.identity.IdentitySignature;
import org.formentor.web3.security.identity.IdentityToken;
import org.formentor.web3.security.identity.IdentityTokenMalformedException;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IdentityTokenTest {

    final String SAMPLE_PRIVATE_KEY = "bf210ebc98c878fbb3699a072094419dbbb716fc2f95d6fb925886d104ac5cb0";
    final String SAMPLE_MESSAGE = "Random message";
    final String SAMPLE_TOKEN = "eyJpZGVudGl0eSI6IjB4NDQ2ODU2NjkyNjBkNTRlMjBhZWNlNDNiZmI3ZmQ5ZjQ3NTAyZDI4ZSIsIm1lc3NhZ2UiOiJSYW5kb20gbWVzc2FnZSIsInYiOiIweDFjIiwiciI6IjB4YWJiZjhmMjNlNWFhODEyNWY1ZDcxMWFiNWFkOTRlOGViYzYxYjNkZGIyNWQ0OTc4YzcyZjY4N2NiNzE4MTUzNyIsInMiOiIweDJmYWMzZGRmYTg5YWE1MDNkZjU5YjdhZmM5NzA1MTEwNjE0OGQwNzVhYmYzNjUwN2RmMzExMzdjYzg5N2NmMzQifQ==";
    final String SAMPLE_TOKEN_DECODED_TOKEN = "{\"identity\":\"0x44685669260d54e20aece43bfb7fd9f47502d28e\",\"message\":\"Random message\",\"v\":\"0x1c\",\"r\":\"0xabbf8f23e5aa8125f5d711ab5ad94e8ebc61b3ddb25d4978c72f687cb7181537\",\"s\":\"0x2fac3ddfa89aa503df59b7afc97051106148d075abf36507df31137cc897cf34\"}";

    @Test
    void decode_Base64IdentityTokenShouldReturnIdentityToken() {
        final byte[] decodeContent = IdentityToken.decode(SAMPLE_TOKEN);

        assertEquals(SAMPLE_TOKEN_DECODED_TOKEN, new String(decodeContent));
    }

    @Test
    void parse_ShouldReturnIdentitySignature() {
        Object identitySignature = IdentityToken.parse(SAMPLE_TOKEN_DECODED_TOKEN.getBytes());

        assert(identitySignature.getClass().isAssignableFrom(IdentitySignature.class));
    }

    @Test
    void parse_InvalidTokenShouldThrowException() {
        final String tokenDecodedContent = "{invalid-token-content}";

        assertThrows(IdentityTokenMalformedException.class,
                () -> IdentityToken.parse(tokenDecodedContent.getBytes()));
    }

    @Test
    void create_ShouldReturnIdentityToken() {
        String identityToken = IdentityToken.create()
                .withMessage(SAMPLE_MESSAGE)
                .withCredentials(Credentials.create(SAMPLE_PRIVATE_KEY))
                .sign();

        assertNotNull(identityToken);
    }

    @Test
    void isSignatureValid() {
        IdentitySignature signature = IdentityToken.parse(SAMPLE_TOKEN);

        assertTrue(IdentityToken.isSignatureValid(signature));
    }
}
