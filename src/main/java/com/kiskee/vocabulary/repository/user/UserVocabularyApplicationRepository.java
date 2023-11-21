package com.kiskee.vocabulary.repository.user;

import com.kiskee.vocabulary.model.entity.user.UserVocabularyApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserVocabularyApplicationRepository extends JpaRepository<UserVocabularyApplication, UUID> {

    boolean existsByUsernameOrEmail(String username, String email);

}