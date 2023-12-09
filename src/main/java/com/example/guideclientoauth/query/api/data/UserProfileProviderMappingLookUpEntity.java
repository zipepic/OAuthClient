package com.example.guideclientoauth.query.api.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class UserProfileProviderMappingLookUpEntity {
    @Id
    private String userId;
    private String githubId;
    private String googleId;
}
