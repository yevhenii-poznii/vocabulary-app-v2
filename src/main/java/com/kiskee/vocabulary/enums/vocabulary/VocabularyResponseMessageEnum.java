package com.kiskee.vocabulary.enums.vocabulary;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VocabularyResponseMessageEnum {

    DICTIONARY_CREATED("%s dictionary created successfully"),
    DICTIONARY_ALREADY_EXISTS("Dictionary with name %s already exists for user"),
    DICTIONARY_UPDATED("%s dictionary updated successfully"),
    DICTIONARY_DELETED("%s dictionary deleted successfully");

    private final String responseMessage;

}