package com.kiskee.vocabulary.service.vocabulary.word.page.asc;

import com.kiskee.vocabulary.enums.vocabulary.PageFilter;
import com.kiskee.vocabulary.mapper.dictionary.DictionaryPageMapper;
import com.kiskee.vocabulary.model.dto.vocabulary.dictionary.page.DictionaryPageResponseDto;
import com.kiskee.vocabulary.model.dto.vocabulary.word.WordDto;
import com.kiskee.vocabulary.model.dto.vocabulary.word.WordIdDto;
import com.kiskee.vocabulary.model.entity.vocabulary.Word;
import com.kiskee.vocabulary.repository.vocabulary.DictionaryPageRepository;
import com.kiskee.vocabulary.service.vocabulary.word.page.impl.asc.DictionaryPageLoaderAllASC;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DictionaryPageLoaderAllASCTest {

    @InjectMocks
    private DictionaryPageLoaderAllASC dictionaryPageLoaderAllASC;

    @Mock
    private DictionaryPageRepository repository;
    @Mock
    private DictionaryPageMapper mapper;

    @Test
    void testGetPageFilter_WhenDictionaryPageLoaderAllASC_ThenReturnPageFilterBY_ADDED_AT_ASC() {
        PageFilter pageFilter = dictionaryPageLoaderAllASC.getPageFilter();

        assertThat(pageFilter).isEqualTo(PageFilter.BY_ADDED_AT_ASC);
    }

    @Test
    void testLoadDictionaryPage_WhenGivenValidDictionaryIdAndPageRequest_ThenReturnDictionaryPageResponseDto() {
        Long dictionaryId = 1L;
        PageRequest pageRequest = PageRequest.of(0, 100);

        Page page = mock(Page.class);
        List<WordIdDto> wordIdDtos = List.of(new WordIdDto(1L), new WordIdDto(2L));
        when(page.stream()).thenReturn(wordIdDtos.stream());
        when(page.getTotalPages()).thenReturn(1);
        when(page.getTotalElements()).thenReturn(2L);

        when(repository.findByDictionaryId(eq(dictionaryId), any())).thenReturn(page);

        List<Word> words = List.of(
                new Word(1L, "word1", true, 0, "hint1", null, null, 1L, List.of()),
                new Word(2L, "word2", false, 0, "hin2", null, null, 1L, List.of())
        );
        when(repository.findByIdInOrderByAddedAtAsc(List.of(1L, 2L))).thenReturn(words);

        List<WordDto> wordDtos = List.of(
                new WordDto(1L, "word1", true, List.of(), "hin1"),
                new WordDto(2L, "word2", false, List.of(), "hint2")
        );
        DictionaryPageResponseDto expectedResult = new DictionaryPageResponseDto(wordDtos,
                page.getTotalPages(), (int) page.getTotalElements());
        when(mapper.toDto(words, page.getTotalPages(), page.getTotalElements())).thenReturn(expectedResult);

        DictionaryPageResponseDto result = dictionaryPageLoaderAllASC.loadDictionaryPage(dictionaryId, pageRequest);

        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getWords()).extracting(WordDto::getWord)
                .containsExactlyInAnyOrder("word1", "word2");
    }

}