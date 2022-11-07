import org.formentor.web3.security.identity.IdentitySignature;
import org.formentor.web3.security.identity.IdentityToken;
import org.formentor.web3.security.identity.Web3AuthenticationProvider;
import org.formentor.web3.security.identity.Web3AuthenticationToken;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Web3AuthenticationProviderTest {
    final String SAMPLE_IDENTITY = "0x44685669260d54e20aece43bfb7fd9f47502d28e";
    final String SAMPLE_TOKEN = "eyJpZGVudGl0eSI6IjB4NDQ2ODU2NjkyNjBkNTRlMjBhZWNlNDNiZmI3ZmQ5ZjQ3NTAyZDI4ZSIsIm1lc3NhZ2UiOiJSYW5kb20gbWVzc2FnZSIsInYiOiIweDFjIiwiciI6IjB4YWJiZjhmMjNlNWFhODEyNWY1ZDcxMWFiNWFkOTRlOGViYzYxYjNkZGIyNWQ0OTc4YzcyZjY4N2NiNzE4MTUzNyIsInMiOiIweDJmYWMzZGRmYTg5YWE1MDNkZjU5YjdhZmM5NzA1MTEwNjE0OGQwNzVhYmYzNjUwN2RmMzExMzdjYzg5N2NmMzQifQ==";

    @Test
    void authenticateIdentityToken() {
        final Web3AuthenticationToken token = new Web3AuthenticationToken(SAMPLE_TOKEN);
        final Web3AuthenticationProvider provider = new Web3AuthenticationProvider();

        final Authentication authentication = provider.authenticate(token);

        assertTrue(Web3AuthenticationToken.class.isAssignableFrom(authentication.getClass()));
        assertTrue(authentication.isAuthenticated());
        assertEquals(SAMPLE_IDENTITY, authentication.getPrincipal());
    }

    @Test
    void authenticateIdentityTokenInvalid() {
        final IdentitySignature signature = IdentitySignature.builder()
                .identity("0x44685669260d54e20aece43bfb7fd9f47502d28e")
                .message("i-am-a-lying")
                .v("0x1c")
                .r("0x39d6fa8ac3094d9c730c28aa48b51cb39a393d0125deff2a492324c28c5a4c76")
                .s("0x038cb5f4bdb0f74b42125e15e55f4a47e9ed67e71ce3a1d1286c19ea4316ecb3")
                .build();
        final String tokenString = IdentityToken.encode(signature);
        final Web3AuthenticationToken token = new Web3AuthenticationToken(tokenString);
        final Web3AuthenticationProvider provider = new Web3AuthenticationProvider();

        assertThrows(BadCredentialsException.class, () -> provider.authenticate(token));
    }
}
