package com.example.guideclientoauth.config;

import com.example.guideclientoauth.query.api.data.UserProfileProviderMappingLookUpRepository;
import jakarta.servlet.Filter;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final QueryGateway queryGateway;
    private final CommandGateway commandGateway;
    private final UserProfileProviderMappingLookUpRepository userProfileProviderMappingLookUpRepository;
    private final AuthProviderImp authProviderImp;
    @Autowired
    public SecurityConfig(QueryGateway queryGateway, CommandGateway commandGateway, UserProfileProviderMappingLookUpRepository userProfileProviderMappingLookUpRepository, AuthProviderImp authProviderImp) {
        this.queryGateway = queryGateway;
        this.commandGateway = commandGateway;
        this.userProfileProviderMappingLookUpRepository = userProfileProviderMappingLookUpRepository;
        this.authProviderImp = authProviderImp;
    }
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(request->{
                    request.requestMatchers("/").permitAll();
                    request.anyRequest().authenticated();
                        })

                .oauth2Login(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())
                .addFilterAfter(customAuthenticationSuccessHandler(), OAuth2LoginAuthenticationFilter.class)
                .authenticationProvider(authProviderImp)
                .build();
    }
    @Bean
    public Filter customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler(commandGateway, queryGateway, userProfileProviderMappingLookUpRepository);
    }
}
