package com.kiskee.vocabulary.model.dto.registration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class UserRegisterResponseDto {

    private String responseMessage;

}