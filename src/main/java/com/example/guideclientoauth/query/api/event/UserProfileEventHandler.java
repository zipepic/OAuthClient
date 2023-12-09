package com.example.guideclientoauth.query.api.event;

import com.example.guideclientoauth.query.api.data.UserProfileProviderMappingLookUpEntity;
import com.example.guideclientoauth.query.api.data.UserProfileProviderMappingLookUpRepository;
import com.project.core.events.user.UserProfileProviderMappingLookUpCreatedEvent;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tokenlib.util.jwk.AuthProvider;

@Component
public class UserProfileEventHandler {
    private final UserProfileProviderMappingLookUpRepository userProfileProviderMappingLookUpRepository;
    @Autowired
    public UserProfileEventHandler(UserProfileProviderMappingLookUpRepository userProfileProviderMappingLookUpRepository) {
        this.userProfileProviderMappingLookUpRepository = userProfileProviderMappingLookUpRepository;
    }

    @EventHandler
     public void on(UserProfileProviderMappingLookUpCreatedEvent event) {
        var entity = new UserProfileProviderMappingLookUpEntity();
        if(event.getAuthProvider() == AuthProvider.GITHUB){
            entity.setGithubId(event.getProviderId());
        }else if(event.getAuthProvider() == AuthProvider.GOOGLE){
            entity.setGoogleId(event.getProviderId());
        }
        System.out.println("event.getProviderId() = " + event.getProviderId());
        userProfileProviderMappingLookUpRepository.save(entity);
     }
}
