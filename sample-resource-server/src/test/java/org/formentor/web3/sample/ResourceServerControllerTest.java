package org.formentor.web3.sample;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResourceServerController.class)
@Import(SecurityConfiguration.class)
public class ResourceServerControllerTest {

    final String SAMPLE_WEB3_TOKEN = "eyJpZGVudGl0eSI6IjB4NDQ2ODU2NjkyNjBkNTRlMjBhZWNlNDNiZmI3ZmQ5ZjQ3NTAyZDI4ZSIsIm1lc3NhZ2UiOiJSYW5kb20gbWVzc2FnZSIsInYiOiIweDFjIiwiciI6IjB4YWJiZjhmMjNlNWFhODEyNWY1ZDcxMWFiNWFkOTRlOGViYzYxYjNkZGIyNWQ0OTc4YzcyZjY4N2NiNzE4MTUzNyIsInMiOiIweDJmYWMzZGRmYTg5YWE1MDNkZjU5YjdhZmM5NzA1MTEwNjE0OGQwNzVhYmYzNjUwN2RmMzExMzdjYzg5N2NmMzQifQ==";

    @Autowired
    MockMvc mockMvc;

    @Test
    public void publicResourceIsAvailableForPublic() throws Exception {
        this.mockMvc
                .perform(get("/public"))
                .andExpect(status().isOk());
    }

    @Test
    public void privateWeb2ResourceRequestWhenAuthenticated() throws Exception {
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("double")
                .password("helix")
                .roles("ADMIN")
                .build();
        this.mockMvc
                .perform(get("/private/web2/enzyme").with(user(admin)))
                .andExpect(status().isOk());
    }

    @Test
    public void privateWeb2ResourceRequestWhenUnAuthenticatedThenIsUnauthorized() throws Exception {
        this.mockMvc
                .perform(get("/private/web2/enzyme"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void privateWeb2ResourceRequestWhenUnAuthorizedThenIsForbidden() throws Exception {
        UserDetails userNonAuthorized = User.withDefaultPasswordEncoder()
                .username("double")
                .password("helix")
                .roles("NONCE")
                .build();
        this.mockMvc
                .perform(get("/private/web2/enzyme").with(user(userNonAuthorized)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void privateWeb3ResourceRequestWhenMissingToken() throws Exception {
        this.mockMvc
                .perform(get("/private/web3/enzyme"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void privateWeb3ResourceRequestWhenAuthenticated() throws Exception {
        final String authorizationHeader = String.format("Bearer %s", SAMPLE_WEB3_TOKEN);
        this.mockMvc
                .perform(get("/private/web3/enzyme").header("Authorization", authorizationHeader))
                .andExpect(status().isOk());
    }

}
