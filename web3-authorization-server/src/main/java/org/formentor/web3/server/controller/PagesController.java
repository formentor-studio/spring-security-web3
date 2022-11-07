package org.formentor.web3.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.web3j.utils.Numeric;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

@Controller
@Slf4j
public class PagesController {
    @GetMapping("/authorize")
    public String status(Model model) {
        String message = String.format("Sign this message to prove you have access to this wallet and we'll provide your access token: %s", generateUUID());
        model.addAttribute("toBeSigned", message);
        return "authorize";
    }

    private String generateUUID() {
        UUID uuid = UUID.randomUUID();
        try {
            MessageDigest salt = MessageDigest.getInstance("SHA-256");
            salt.update(uuid.toString().getBytes(StandardCharsets.UTF_8));
            return Numeric.toHexStringNoPrefix(salt.digest());
        } catch (Exception e) {
            log.error("Errors hashing the UUID {}. Using directly the value", uuid);
            return uuid.toString();
        }
    }
}
