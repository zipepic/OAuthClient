package com.example.guideclientoauth.config;

import com.project.core.queries.user.FindUserIdByUserNameAndValidatePasswordQuery;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationProvider;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component
public class AuthProviderImp implements AuthenticationProvider {
    private final QueryGateway queryGateway;

    public AuthProviderImp(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        if(authentication instanceof OAuth2LoginAuthenticationToken){
//            var user = new DefaultOAuth2User(null, Map.of("name", authentication.getPrincipal()), "name");
//            var token = new OAuth2AuthenticationToken(user, null,((OAuth2LoginAuthenticationToken) authentication).getClientRegistration().getRegistrationId());
//            return token;
//        }
        try {
            var query = FindUserIdByUserNameAndValidatePasswordQuery.builder()
                    .userName(authentication.getName())
                    .password(authentication.getCredentials().toString())
                    .build();
            String userId = queryGateway.query(query, String.class).join();
            var auth = new UsernamePasswordAuthenticationToken(userId, authentication.getCredentials(), authentication.getAuthorities());
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            return auth;
        } catch (Exception e) {
            throw new RuntimeException("User not found or password not match");
        }
    }
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
