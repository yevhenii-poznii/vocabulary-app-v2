package com.kiskee.dictionarybuilder.service.user.preference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kiskee.dictionarybuilder.config.properties.user.DefaultUserPreferenceProperties;
import com.kiskee.dictionarybuilder.enums.user.ProfileVisibility;
import com.kiskee.dictionarybuilder.enums.vocabulary.PageFilter;
import com.kiskee.dictionarybuilder.mapper.user.preference.UserPreferenceMapper;
import com.kiskee.dictionarybuilder.model.dto.registration.InternalRegistrationRequest;
import com.kiskee.dictionarybuilder.model.dto.user.preference.WordPreference;
import com.kiskee.dictionarybuilder.model.entity.user.UserVocabularyApplication;
import com.kiskee.dictionarybuilder.model.entity.user.preference.UserPreference;
import com.kiskee.dictionarybuilder.repository.user.preference.UserPreferenceRepository;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserPreferenceServiceTest {

    @InjectMocks
    private UserPreferenceService userPreferenceService;

    @Mock
    private UserPreferenceRepository userPreferenceRepository;

    @Mock
    private UserPreferenceMapper userPreferenceMapper;

    @Mock
    private DefaultUserPreferenceProperties defaultUserPreferenceProperties;

    @Captor
    private ArgumentCaptor<UserPreference> userPreferenceArgumentCaptor;

    @Test
    void testInitDefault_WhenUserVocabularyApplicationIsGiven_ThenBuildAndSaveDefaultUserPreference() {
        UUID userId = UUID.fromString("75ab44f4-40a3-4094-a885-51ade9e6df4a");
        UserVocabularyApplication givenUserEntity = mock(UserVocabularyApplication.class);
        InternalRegistrationRequest registrationRequest = mock(InternalRegistrationRequest.class);
        when(registrationRequest.getUser()).thenReturn(givenUserEntity);

        when(givenUserEntity.getId()).thenReturn(userId);

        UserPreference userPreference = new UserPreference(
                userId,
                ProfileVisibility.PRIVATE,
                100,
                true,
                PageFilter.BY_ADDED_AT_ASC,
                10,
                10,
                Duration.ofHours(1),
                givenUserEntity);
        when(userPreferenceMapper.toEntity(
                        defaultUserPreferenceProperties,
                        givenUserEntity,
                        PageFilter.BY_ADDED_AT_ASC,
                        ProfileVisibility.PRIVATE))
                .thenReturn(userPreference);

        userPreferenceService.initUser(registrationRequest);

        verify(userPreferenceRepository).save(userPreferenceArgumentCaptor.capture());

        UserPreference actual = userPreferenceArgumentCaptor.getValue();
        assertThat(actual.getUser().getId()).isEqualTo(userId);
        assertThat(actual.getProfileVisibility()).isEqualTo(ProfileVisibility.PRIVATE);
        assertThat(actual.getRightAnswersToDisableInRepetition()).isEqualTo(10);
        assertThat(actual.getWordsPerPage()).isEqualTo(100);
        assertThat(actual.isBlurTranslation()).isTrue();
    }

    @Test
    void testWordPreference_WhenUserIdIsGiven_ThenReturnWordPreference() {
        UUID userId = UUID.fromString("75ab44f4-40a3-4094-a885-51ade9e6df4a");
        int rightAnswersToDisableInRepetition = 10;
        int newWordsPerDayGoal = 10;
        WordPreference expectedWordPreference =
                new WordPreference(rightAnswersToDisableInRepetition, newWordsPerDayGoal, Duration.ofHours(1));
        when(userPreferenceRepository.findWordPreferenceByUserId(userId)).thenReturn(expectedWordPreference);

        WordPreference actualWordPreference = userPreferenceService.getWordPreference(userId);

        assertThat(actualWordPreference.rightAnswersToDisableInRepetition())
                .isEqualTo(rightAnswersToDisableInRepetition);
        assertThat(actualWordPreference.newWordsPerDayGoal()).isEqualTo(newWordsPerDayGoal);
    }
}
