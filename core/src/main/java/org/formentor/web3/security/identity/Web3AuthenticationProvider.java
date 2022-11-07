package org.formentor.web3.security.identity;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.ObjectUtils;

public class Web3AuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getPrincipal();

        if (ObjectUtils.isEmpty(token)) {
            throw new InsufficientAuthenticationException("Missing token in request");
        } else {
            final IdentitySignature identitySignature = IdentityToken.parse(token);
            if (IdentityToken.isSignatureValid(identitySignature)) {
                return new Web3AuthenticationToken(identitySignature.getIdentity(), true);
            }
            throw new BadCredentialsException("Invalid token signature");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return Web3AuthenticationToken.class.isAssignableFrom(authentication);
    }
}
