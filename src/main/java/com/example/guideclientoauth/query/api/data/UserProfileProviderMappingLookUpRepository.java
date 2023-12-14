package com.example.guideclientoauth.query.api.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileProviderMappingLookUpRepository extends JpaRepository<UserProfileProviderMappingLookUpEntity, String> {
    Optional<UserProfileProviderMappingLookUpEntity> findByGithubId(String githubId);
    Optional<UserProfileProviderMappingLookUpEntity> findByGoogleId(String googleId);
}
