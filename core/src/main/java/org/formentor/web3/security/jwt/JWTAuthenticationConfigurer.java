package org.formentor.web3.security.jwt;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import java.util.Collections;

public class JWTAuthenticationConfigurer extends AbstractHttpConfigurer<JWTAuthenticationConfigurer, HttpSecurity> {
    @Override
    public void init(HttpSecurity http) {
        // initialization code
    }

    @Override
    public void configure(HttpSecurity http) {
        AuthenticationManager authenticationManagerWeb3 = new ProviderManager(Collections.singletonList(new JWTAuthenticationProvider()));
        http.addFilterBefore(
                new JWTAuthenticationFilter(authenticationManagerWeb3),
                AnonymousAuthenticationFilter.class
        );

    }
}
