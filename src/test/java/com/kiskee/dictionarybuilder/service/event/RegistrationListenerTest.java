package com.kiskee.dictionarybuilder.service.event;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kiskee.dictionarybuilder.enums.user.UserRole;
import com.kiskee.dictionarybuilder.model.entity.user.UserVocabularyApplication;
import com.kiskee.dictionarybuilder.repository.user.projections.UserSecureProjection;
import com.kiskee.dictionarybuilder.service.email.EmailSenderService;
import com.kiskee.dictionarybuilder.service.token.TokenPersistenceService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RegistrationListenerTest {

    @InjectMocks
    private RegistrationListener registrationListener;

    @Mock
    private TokenPersistenceService<UUID, String> tokenService;

    @Mock
    private EmailSenderService emailSenderService;

    @Test
    void testGenerateVerificationTokenAndSendEmail_When_Then() {
        UUID userId = UUID.fromString("36effc62-d93a-4451-9f7b-7cf82de0d326");
        UserSecureProjection userSecureProjection = new UserVocabularyApplication(
                userId, "someEmail@gmail.com", "username", null, false, UserRole.ROLE_USER, null, null);
        OnRegistrationCompleteEvent event = new OnRegistrationCompleteEvent(userSecureProjection);

        String verificationTokenDto = "someVerificationToken";
        when(tokenService.persistToken(userId)).thenReturn(verificationTokenDto);

        registrationListener.onApplicationEvent(event);

        verify(tokenService).persistToken(userId);
        verify(emailSenderService).sendVerificationEmail(userSecureProjection, verificationTokenDto);
    }
}
