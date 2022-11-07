package org.formentor.web3.security.identity;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

public class Web3AuthenticationToken extends AbstractAuthenticationToken {
    private String token;

    public Web3AuthenticationToken() {
        super(AuthorityUtils.NO_AUTHORITIES);
        setAuthenticated(false);
    }

    public Web3AuthenticationToken(String token) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.token = token;
        setAuthenticated(false);
    }

    public Web3AuthenticationToken(String token, boolean authenticated) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.token = token;
        setAuthenticated(authenticated);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }
}
