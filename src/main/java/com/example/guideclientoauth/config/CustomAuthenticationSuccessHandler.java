package com.example.guideclientoauth.config;

import com.example.guideclientoauth.query.api.data.UserProfileProviderMappingLookUpEntity;
import com.example.guideclientoauth.query.api.data.UserProfileProviderMappingLookUpRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.core.commands.token.GenerateJwtTokenCommand;
import com.project.core.commands.user.CreateUserFromProviderIdCommand;
import com.project.core.dto.TokenDTO;
import com.project.core.dto.TokenId;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;
import tokenlib.util.jwk.AuthProvider;

import java.io.IOException;
import java.util.UUID;
import java.util.stream.Collectors;

public class CustomAuthenticationSuccessHandler extends OncePerRequestFilter {
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private final UserProfileProviderMappingLookUpRepository userProfileProviderMappingLookUpRepository;

    public CustomAuthenticationSuccessHandler(CommandGateway commandGateway, QueryGateway queryGateway, UserProfileProviderMappingLookUpRepository userProfileProviderMappingLookUpRepository) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
        this.userProfileProviderMappingLookUpRepository = userProfileProviderMappingLookUpRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

            String provider = oauthToken.getAuthorizedClientRegistrationId();
            System.out.println("All good 1");
            UserProfileProviderMappingLookUpEntity userProfileEntity = getUserProfileEntity(provider, oauthToken.getName());
            System.out.println("All good 2");
            //Если пользователь не найден в локальной БД, то создаем его
            //TODO add check for user in auth server
            if(userProfileEntity == null) {
                createUserFromProviderId(provider, oauthToken.getName());
            }
            System.out.println("All good 3");
            var command = GenerateJwtTokenCommand.builder()
                    .tokenFromUserId(new TokenId(userProfileEntity.getUserId()))
                    .build();
            TokenDTO tokenDTO = commandGateway.sendAndWait(command);
            System.out.println("All good 4");
            writeTokenInResponse(response, tokenDTO);
        }
        if(authentication != null && authentication.isAuthenticated()) {
            System.out.println("User " + authentication.getName() + " authenticated");
            var command = GenerateJwtTokenCommand.builder()
                    .tokenFromUserId(new TokenId(authentication.getName()))
                    .build();
            TokenDTO tokenDTO = commandGateway.sendAndWait(command);
            writeTokenInResponse(response, tokenDTO);
        }
        filterChain.doFilter(request, response);
    }

    private static void writeTokenInResponse(HttpServletResponse response, TokenDTO tokenDTO) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String tokenJson = objectMapper.writeValueAsString(tokenDTO);

        response.setContentType("application/json");

        response.getWriter().write(tokenJson);
        response.getWriter().flush();
    }

    private UserProfileProviderMappingLookUpEntity getUserProfileEntity(String provider, String oauthTokenName) {
        UserProfileProviderMappingLookUpEntity userProfileEntity = null;
        if("github".equals(provider)) {
            userProfileEntity = userProfileProviderMappingLookUpRepository.findAll().stream().filter(x-> oauthTokenName.equals(x.getGithubId())).collect(Collectors.toList()).get(0);
        } else if("google".equals(provider)) {
            userProfileEntity = userProfileProviderMappingLookUpRepository.findAll().stream().filter(x-> oauthTokenName.equals(x.getGoogleId())).collect(Collectors.toList()).get(0);
        }
        return userProfileEntity;
    }

    private void createUserFromProviderId(String provider, String oauthTokenName) {
        var command = CreateUserFromProviderIdCommand.builder()
                .userId(UUID.randomUUID().toString())
                .userName(oauthTokenName)
                .providerId(oauthTokenName)
                .authProvider("github".equals(provider) ? AuthProvider.GITHUB : AuthProvider.GOOGLE)
                .build();
        commandGateway.send(command);
        System.out.println(provider + " authentication: " + oauthTokenName);
    }

}
