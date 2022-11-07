package org.formentor.web3.security.identity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import java.util.Collections;

public class Web3AuthenticationConfigurer extends AbstractHttpConfigurer<Web3AuthenticationConfigurer, HttpSecurity> {
    @Override
    public void init(HttpSecurity http) {
        // initialization code
    }

    @Override
    public void configure(HttpSecurity http) {
        AuthenticationManager authenticationManagerWeb3 = new ProviderManager(Collections.singletonList(new Web3AuthenticationProvider()));
        http.addFilterBefore(
                new Web3AuthenticationFilter(authenticationManagerWeb3),
                AnonymousAuthenticationFilter.class
        );

    }
}
