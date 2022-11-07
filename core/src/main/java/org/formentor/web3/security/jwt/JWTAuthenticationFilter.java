package org.formentor.web3.security.jwt;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JWTAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final Pattern authorizationPattern = Pattern.compile("^Bearer (?<token>[a-zA-Z0-9-._~+/]+=*)$",
            Pattern.CASE_INSENSITIVE);

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        super("/**", authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        Optional<String> bearerToken = deriveBearerTokenFromAuthorizationHeader(request);
        BearerTokenAuthentication token =
                bearerToken.map(BearerTokenAuthentication::new).orElse(new BearerTokenAuthentication());

        return getAuthenticationManager().authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        chain.doFilter(request, response);
    }

    /**
     * Derives token from Authorization Header
     *
     * @param request Http request
     * @return bearer token
     */
    private Optional<String> deriveBearerTokenFromAuthorizationHeader(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.startsWithIgnoreCase(authorization, "bearer")) {
            return Optional.empty();
        }
        Matcher matcher = authorizationPattern.matcher(authorization);
        if (!matcher.matches()) {
            throw new JWTAuthenticationException("Bearer token is malformed");
        }
        return Optional.ofNullable(matcher.group("token"));
    }

}
