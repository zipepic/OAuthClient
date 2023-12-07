package com.example.guideclientoauth;

import com.project.core.commands.user.*;
import com.project.core.queries.user.CheckUserProfileByProviderIdQuery;
import com.thoughtworks.xstream.XStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GuideClientOauthApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuideClientOauthApplication.class, args);
    }
    @Bean
    public XStream xStream() {
        XStream xStream = new XStream();
        registerClasses(xStream,
                CreateUserProfileCommand.class,
                CheckUserProfileByProviderIdQuery.class,
                BindProviderIdToUserCommand.class,
                CreateUserFromProviderIdCommand.class);

        return xStream;
    }

    private void registerClasses(XStream xStream, Class<?>... classes) {
        for (Class<?> clazz : classes) {
            xStream.allowTypeHierarchy(clazz);
        }
    }
}
