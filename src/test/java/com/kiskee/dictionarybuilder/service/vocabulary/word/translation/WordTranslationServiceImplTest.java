package com.kiskee.dictionarybuilder.service.vocabulary.word.translation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.kiskee.dictionarybuilder.mapper.dictionary.WordTranslationMapper;
import com.kiskee.dictionarybuilder.model.dto.vocabulary.word.WordTranslationDto;
import com.kiskee.dictionarybuilder.model.entity.vocabulary.WordTranslation;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class WordTranslationServiceImplTest {

    @InjectMocks
    private WordTranslationServiceImpl wordTranslationService;

    @Mock
    private WordTranslationMapper mapper;

    @Test
    void testUpdateTranslations_WhenGivenNewTranslationsHaveNullId_ThenUpdateTranslations() {
        List<WordTranslationDto> translationsToUpdate = List.of(
                new WordTranslationDto(1L, "переклад1"),
                new WordTranslationDto(2L, "переклад2"),
                new WordTranslationDto(null, "новий переклад1"),
                new WordTranslationDto(null, "новий переклад2"));
        List<WordTranslation> existingTranslations =
                List.of(new WordTranslation(1L, "переклад1"), new WordTranslation(2L, "переклад2"));

        List<WordTranslation> translationToUpdateEntities =
                List.of(new WordTranslation(null, "новий переклад1"), new WordTranslation(null, "новий переклад2"));

        when(mapper.toEntities(translationsToUpdate)).thenReturn(translationToUpdateEntities);

        List<WordTranslation> expectedUpdatedTranslations = List.of(
                new WordTranslation(1L, "переклад1"),
                new WordTranslation(2L, "переклад2"),
                new WordTranslation(null, "новий переклад1"),
                new WordTranslation(null, "новий переклад2"));

        List<WordTranslation> updatedTranslations =
                wordTranslationService.updateTranslations(translationsToUpdate, existingTranslations);

        assertThat(updatedTranslations).containsExactlyInAnyOrderElementsOf(expectedUpdatedTranslations);
    }

    @Test
    void
            testUpdateTranslations_WhenNewTranslationsHaveIdThatDoesNotExistInExistingTranslations_ThenUpdateTranslationsWithoutNotExistingId() {
        List<WordTranslationDto> translationsToUpdate = List.of(
                new WordTranslationDto(1L, "переклад1"),
                new WordTranslationDto(2L, "переклад2"),
                new WordTranslationDto(null, "новий переклад1"),
                new WordTranslationDto(null, "новий переклад2"),
                new WordTranslationDto(3L, "переклад з неіснуючим id"));
        List<WordTranslation> existingTranslations =
                List.of(new WordTranslation(1L, "переклад1"), new WordTranslation(2L, "переклад2"));

        List<WordTranslation> translationToUpdateEntities =
                List.of(new WordTranslation(null, "новий переклад1"), new WordTranslation(null, "новий переклад2"));

        when(mapper.toEntities(translationsToUpdate)).thenReturn(translationToUpdateEntities);

        List<WordTranslation> expectedUpdatedTranslations = List.of(
                new WordTranslation(1L, "переклад1"),
                new WordTranslation(2L, "переклад2"),
                new WordTranslation(null, "новий переклад1"),
                new WordTranslation(null, "новий переклад2"));

        List<WordTranslation> updatedTranslations =
                wordTranslationService.updateTranslations(translationsToUpdate, existingTranslations);

        assertThat(updatedTranslations).containsExactlyInAnyOrderElementsOf(expectedUpdatedTranslations);
    }
}
