package org.formentor.web3.security.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;

public class BearerTokenAuthentication extends AbstractAuthenticationToken {
    private String principal;

    public BearerTokenAuthentication() {
        super(AuthorityUtils.NO_AUTHORITIES);
        setAuthenticated(false);
    }

    public BearerTokenAuthentication(String token) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.principal = token;
        setAuthenticated(false);
    }

    public BearerTokenAuthentication(String principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.setAuthenticated(true);
        this.principal = principal;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
