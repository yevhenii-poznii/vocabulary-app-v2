package com.kiskee.dictionarybuilder.repository.user;

import com.kiskee.dictionarybuilder.model.entity.user.UserVocabularyApplication;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserVocabularyApplication, UUID> {

    boolean existsByUsernameOrEmail(String username, String email);

    Optional<UserVocabularyApplication> findByUsernameOrEmail(String username, String email);

    Optional<UserVocabularyApplication> findByEmail(String email);
}
