package com.example.guideclientoauth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;
    @GetMapping("/")
    public String home() {
        return ("<h1>Welcome</h1>");
    }
    @GetMapping("/secured")
    public String secured(Principal principal) {
        return ("<h1>Secured</h1>" + principal.toString());
    }
    @GetMapping("/user")
    public String home(Principal principal) {

        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient("github", principal.getName());
        Map<String, String> map = new HashMap<>();
        map.put("getPrincipalName", authorizedClient.getPrincipalName());
        map.put("accessTokenType", authorizedClient.getAccessToken().getTokenType().getValue());
        map.put("Access token-> ", authorizedClient.getAccessToken().getTokenValue());
        map.put("clientRegis", authorizedClient.getClientRegistration().getClientId());

        return ("<h1>Welcome</h1>" + "Access Token: " + map);
    }
}
