package com.kiskee.vocabulary.service.registration;

import com.kiskee.vocabulary.enums.registration.RegistrationStatus;
import com.kiskee.vocabulary.model.dto.ResponseMessage;
import com.kiskee.vocabulary.model.dto.registration.RegistrationRequest;
import com.kiskee.vocabulary.model.entity.token.VerificationToken;
import com.kiskee.vocabulary.model.entity.user.UserVocabularyApplication;
import com.kiskee.vocabulary.service.event.OnRegistrationCompleteEvent;
import com.kiskee.vocabulary.service.token.TokenInvalidatorService;
import com.kiskee.vocabulary.service.user.UserProvisioningService;
import com.kiskee.vocabulary.service.user.UserRegistrationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final PasswordEncoder passwordEncoder;
    private final UserRegistrationService userRegistrationService;
    private final List<UserProvisioningService> userProvisioningServices;
    private final TokenInvalidatorService<VerificationToken> tokenInvalidatorService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public ResponseMessage registerUserAccount(RegistrationRequest userRegisterRequest) {
        String hashedPassword = passwordEncoder.encode(userRegisterRequest.getRawPassword());
        userRegisterRequest.setHashedPassword(hashedPassword);

        UserVocabularyApplication userAccount = buildUserAccount(userRegisterRequest);

        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(userAccount));

        return new ResponseMessage(String.format(RegistrationStatus.USER_SUCCESSFULLY_CREATED.getStatus(),
                userAccount.getEmail()));
    }

    @Override
    @Transactional
    public ResponseMessage completeRegistration(String verificationToken) {
        VerificationToken foundToken = tokenInvalidatorService.findTokenOrThrow(verificationToken);

        userRegistrationService.updateUserAccountToActive(foundToken.getUserId());

        tokenInvalidatorService.invalidateToken(foundToken);

        log.info("User account [{}] has been successfully activated", foundToken.getUserId());

        return new ResponseMessage(RegistrationStatus.USER_SUCCESSFULLY_ACTIVATED.getStatus());
    }

    private UserVocabularyApplication buildUserAccount(RegistrationRequest userRegisterRequest) {
        UserVocabularyApplication createdUser = userRegistrationService.createNewUser(userRegisterRequest);

        userProvisioningServices.forEach(provision -> provision.initDefault(createdUser));

        return createdUser;
    }

}
