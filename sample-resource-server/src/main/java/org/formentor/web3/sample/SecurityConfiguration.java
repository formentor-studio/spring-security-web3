package org.formentor.web3.sample;

import org.formentor.web3.security.identity.Web3AuthenticationConfigurer;
import org.formentor.web3.security.jwt.JWTAuthenticationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    @Order(0)
    public SecurityFilterChain filterChainWebPublic(HttpSecurity http) throws Exception {
        http
                .requestMatchers(requestMatcherConfigurer -> requestMatcherConfigurer.antMatchers("/public/**"))
                .authorizeRequests((authz) -> authz
                        .anyRequest().permitAll()
                );
        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain filterChainWeb3_0(HttpSecurity http) throws Exception {
        http
                .requestMatchers(requestMatcherConfigurer -> requestMatcherConfigurer.antMatchers("/private/web3/**"))
                .authorizeRequests((authz) -> authz
                        .anyRequest().authenticated()
                )
                .apply(new Web3AuthenticationConfigurer());
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain filterChainJwt(HttpSecurity http) throws Exception {
        http
                .requestMatchers(requestMatcherConfigurer -> requestMatcherConfigurer.antMatchers("/private/jwt/**"))
                .authorizeRequests((authz) -> authz
                        .anyRequest().authenticated()
                )
                .apply(new JWTAuthenticationConfigurer());
        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain filterChainWeb2_0(HttpSecurity http) throws Exception {
        http
                .requestMatchers(requestMatcherConfigurer -> requestMatcherConfigurer.antMatchers("/private/web2/**"))
                .authorizeHttpRequests((authz) -> authz
                        .antMatchers("/private/web2/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        // For testing purposes
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("double")
                .password("helix")
                .roles("ADMIN")
                .build();
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("crispr")
                .password("cas9")
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }

}
