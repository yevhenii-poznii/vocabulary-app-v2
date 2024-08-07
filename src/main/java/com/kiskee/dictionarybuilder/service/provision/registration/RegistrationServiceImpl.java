package com.kiskee.dictionarybuilder.service.provision.registration;

import com.kiskee.dictionarybuilder.enums.registration.RegistrationStatus;
import com.kiskee.dictionarybuilder.exception.token.InvalidVerificationTokenException;
import com.kiskee.dictionarybuilder.model.dto.ResponseMessage;
import com.kiskee.dictionarybuilder.model.dto.registration.InternalRegistrationRequest;
import com.kiskee.dictionarybuilder.model.entity.token.VerificationToken;
import com.kiskee.dictionarybuilder.model.entity.user.UserVocabularyApplication;
import com.kiskee.dictionarybuilder.service.event.OnRegistrationCompleteEvent;
import com.kiskee.dictionarybuilder.service.provision.AbstractUserProvisionService;
import com.kiskee.dictionarybuilder.service.token.TokenInvalidatorService;
import com.kiskee.dictionarybuilder.service.user.UserInitializingService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class RegistrationServiceImpl extends AbstractUserProvisionService implements RegistrationService {

    private final PasswordEncoder passwordEncoder;

    @Getter
    private final List<UserInitializingService> userInitializingServices;

    private final TokenInvalidatorService<VerificationToken> tokenInvalidatorService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public ResponseMessage registerUserAccount(InternalRegistrationRequest registrationRequest) {
        String hashedPassword = passwordEncoder.encode(registrationRequest.getRawPassword());
        registrationRequest.setHashedPassword(hashedPassword);

        UserVocabularyApplication userAccount = buildUserAccount(registrationRequest);

        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(userAccount));

        return new ResponseMessage(
                String.format(RegistrationStatus.USER_SUCCESSFULLY_CREATED.getStatus(), userAccount.getEmail()));
    }

    @Override
    @Transactional
    public ResponseMessage completeRegistration(String verificationToken) {
        VerificationToken foundToken = tokenInvalidatorService.findTokenOrThrow(verificationToken);

        validate(foundToken);

        userInitializingServices.getFirst().updateUserAccountToActive(foundToken.getUserId());

        tokenInvalidatorService.invalidateToken(foundToken);

        log.info("User account [{}] has been successfully activated", foundToken.getUserId());

        return new ResponseMessage(RegistrationStatus.USER_SUCCESSFULLY_ACTIVATED.getStatus());
    }

    private void validate(VerificationToken verificationToken) {
        if (verificationToken.isInvalidated()) {
            throw new InvalidVerificationTokenException("Verification token is already invalidated");
        }
    }
}
