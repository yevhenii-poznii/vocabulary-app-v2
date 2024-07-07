package com.kiskee.vocabulary.service.user.profile;

import java.time.Instant;
import java.util.UUID;

public interface UserProfileInfoProvider {

    Instant getCreatedAtField(UUID userId);
}
