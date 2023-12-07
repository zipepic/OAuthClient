package com.example.guideclientoauth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.core.commands.user.BindProviderIdToUserCommand;
import com.project.core.commands.user.CreateUserFromProviderIdCommand;
import com.project.core.commands.user.CreateUserProfileCommand;
import com.project.core.commands.user.GenerateTokenByProviderIdCommand;
import com.project.core.dto.TokenDTO;
import com.project.core.queries.user.CheckUserProfileByProviderIdQuery;
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

import java.io.IOException;
import java.util.UUID;

public class CustomAuthenticationSuccessHandler extends OncePerRequestFilter {
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public CustomAuthenticationSuccessHandler(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            
            String provider = oauthToken.getAuthorizedClientRegistrationId();

            var query = CheckUserProfileByProviderIdQuery.builder()
                    .providerId(oauthToken.getName())
                    .providerType(provider)
                    .build();
            var userProfileId = queryGateway.query(query, String.class).join();

            if(userProfileId == null) {
                if ("github".equals(provider)) {
                    var command = CreateUserFromProviderIdCommand.builder()
                            .userId(UUID.randomUUID().toString())
                            .userName(oauthToken.getName())
                            .providerId(oauthToken.getName())
                            .providerType(provider)
                            .build();
                    commandGateway.sendAndWait(command);
                    System.out.println("GitHub authentication: " + authentication.getName());

                } else if ("google".equals(provider)) {
                    var command = CreateUserFromProviderIdCommand.builder()
                            .userId(UUID.randomUUID().toString())
                            .userName(oauthToken.getName())
                            .providerId(oauthToken.getName())
                            .providerType(provider)
                            .build();
                    commandGateway.sendAndWait(command);
                    System.out.println("Google authentication: " + authentication.getName());
                }
            }
            var command = GenerateTokenByProviderIdCommand.builder()
                    .userId(userProfileId)
                    .providerId(oauthToken.getName())
                    .providerType(provider)
                    .build();
            var tokenDTO = (TokenDTO) commandGateway.sendAndWait(command);
            System.out.println(tokenDTO.getAccessToken());
            response.addHeader("access", tokenDTO.getAccessToken());

            ObjectMapper objectMapper = new ObjectMapper();
            String tokenJson = objectMapper.writeValueAsString(tokenDTO);

            response.setContentType("application/json");

            response.getWriter().write(tokenJson);
            response.getWriter().flush();

        }
        filterChain.doFilter(request, response);
    }
}
