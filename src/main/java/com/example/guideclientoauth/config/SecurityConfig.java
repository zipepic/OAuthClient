package com.example.guideclientoauth.config;

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

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final QueryGateway queryGateway;
    private final CommandGateway commandGateway;
    @Autowired
    public SecurityConfig(QueryGateway queryGateway, CommandGateway commandGateway) {
        this.queryGateway = queryGateway;
        this.commandGateway = commandGateway;
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
                .build();
    }
    @Bean
    public Filter customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler(commandGateway, queryGateway);
    }
}
