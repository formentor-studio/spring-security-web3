package org.formentor.web3.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.stream.Collectors;

public class JWTAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getPrincipal();

        if (ObjectUtils.isEmpty(token)) {
            throw new InsufficientAuthenticationException("Missing token in request");
        } else {
            DecodedJWT jwt = JWT.decode(token);
            return new BearerTokenAuthentication(jwt.getSubject(), getAuthorities(jwt));
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return BearerTokenAuthentication.class.isAssignableFrom(authentication);
    }

    private Collection<GrantedAuthority> getAuthorities(DecodedJWT jwt) {
        Claim scope = jwt.getClaim("scope");
        if (scope.isMissing()) {
            return AuthorityUtils.NO_AUTHORITIES;
        }
        return scope.asList(String.class).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}
