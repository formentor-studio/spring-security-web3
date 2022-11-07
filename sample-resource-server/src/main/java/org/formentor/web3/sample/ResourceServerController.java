package org.formentor.web3.sample;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ResourceServerController {
    @GetMapping("/public")
    public String publicResource() {
        return "public resource";
    }

    @GetMapping("/private/web2/{*resource}")
    public String web2Resource(@PathVariable String resource, @AuthenticationPrincipal User authentication) {
        Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();
        return String.format("private resource:%s %s", (authentication == null)? "anonymous": authentication.getUsername(), resource);
    }

    @GetMapping(path = "/private/web3/{*resource}", produces = "application/json")
    public Resource web3Resource(@PathVariable String resource) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Resource.builder()
                .identity(authentication.getPrincipal().toString())
                .authorities(authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .resource(resource)
                .build();
    }

    @GetMapping(path = "/private/jwt/{*resource}", produces = "application/json")
    public Resource jwtResource(@PathVariable String resource) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Resource.builder()
                .identity(authentication.getPrincipal().toString())
                .authorities(authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .resource(resource)
                .build();
    }

    @Builder
    @Getter
    public static class Resource {
        private String identity;
        private List<String> authorities;
        private String resource;
    }
}
