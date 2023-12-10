package com.example.guideclientoauth;

import com.project.core.commands.user.*;
import com.project.core.dto.TokenAuthorizationCodeDTO;
import com.project.core.events.app.ApplicationCreatedEvent;
import com.project.core.events.user.UserProfileCreatedEvent;
import com.project.core.events.user.UserProfileProviderMappingLookUpCreatedEvent;
import com.project.core.events.user.UserWereCompletedEvent;
import com.project.core.queries.user.CheckUserProfileByProviderIdQuery;
import com.thoughtworks.xstream.XStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import tokenlib.util.jwk.AuthProvider;

@SpringBootApplication
public class GuideClientOauthApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuideClientOauthApplication.class, args);
    }
    @Bean
    public XStream xStream() {
        XStream xStream = new XStream();
//        registerClasses(xStream,
//                CreateUserProfileCommand.class,
//                CheckUserProfileByProviderIdQuery.class,
//                BindProviderIdToUserCommand.class,
//                CreateUserFromProviderIdCommand.class,
//                GenerateTokenByProviderIdCommand.class,
//                TokenAuthorizationCodeDTO.class,
//                UserProfileProviderMappingLookUpCreatedEvent.class,
//                UserProfileCreatedEvent.class,
//                AuthProvider.class,
//                UserWereCompletedEvent.class,
//                ApplicationCreatedEvent.class);

        xStream.allowTypesByWildcard(new String[] {
                "com.project.core.**"
        });

        return xStream;
    }

//    private void registerClasses(XStream xStream, Class<?>... classes) {
//        for (Class<?> clazz : classes) {
//            xStream.allowTypeHierarchy(clazz);
//        }
//    }
}
