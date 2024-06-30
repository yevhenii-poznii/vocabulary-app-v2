package com.kiskee.vocabulary.service.vocabulary.repetition;

import com.kiskee.vocabulary.model.dto.repetition.RepetitionRunningStatus;
import com.kiskee.vocabulary.model.dto.repetition.RepetitionStartFilterRequest;
import com.kiskee.vocabulary.model.dto.repetition.message.WSRequest;
import com.kiskee.vocabulary.model.dto.repetition.message.WSResponse;
import org.springframework.security.core.Authentication;

public interface RepetitionService {

    RepetitionRunningStatus isRepetitionRunning();

    RepetitionRunningStatus start(long dictionaryId, RepetitionStartFilterRequest request);

    RepetitionRunningStatus pause();

    RepetitionRunningStatus unpause();

    RepetitionRunningStatus stop();

    WSResponse handleRepetitionMessage(Authentication principal, WSRequest request);
}
