package com.kiskee.vocabulary.service.user.preference;

import com.kiskee.vocabulary.model.dto.user.WordPreference;
import java.util.UUID;

public interface WordPreferenceService {

    WordPreference getWordPreference(UUID userId);
}
