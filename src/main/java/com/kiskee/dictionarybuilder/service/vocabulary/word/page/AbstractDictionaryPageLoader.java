package com.kiskee.dictionarybuilder.service.vocabulary.word.page;

import com.kiskee.dictionarybuilder.enums.vocabulary.PageFilter;
import com.kiskee.dictionarybuilder.mapper.dictionary.DictionaryPageMapper;
import com.kiskee.dictionarybuilder.model.dto.vocabulary.dictionary.page.DictionaryPageResponseDto;
import com.kiskee.dictionarybuilder.model.dto.vocabulary.word.WordIdDto;
import com.kiskee.dictionarybuilder.model.entity.vocabulary.Word;
import com.kiskee.dictionarybuilder.repository.vocabulary.DictionaryPageRepository;
import com.kiskee.dictionarybuilder.util.IdentityUtil;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Slf4j
@Getter
@AllArgsConstructor
public abstract class AbstractDictionaryPageLoader {

    private final DictionaryPageRepository repository;
    private final DictionaryPageMapper mapper;

    private static final String ORDER_BY = "addedAt";

    public abstract PageFilter getPageFilter();

    protected abstract List<Word> loadWordsByFilter(List<Long> wordIds);

    protected abstract Sort.Direction getSortDirection();

    public DictionaryPageResponseDto loadDictionaryPage(Long dictionaryId, PageRequest pageRequest) {
        return loadPage(dictionaryId, pageRequest);
    }

    protected Page<WordIdDto> findWordIdsPage(Long dictionaryId, Pageable pageable) {
        return repository.findByDictionaryId(dictionaryId, pageable);
    }

    protected Pageable pageableWithSort(PageRequest pageRequest) {
        Sort.Direction sort = getSortDirection();
        return pageRequest.withSort(sort, ORDER_BY);
    }

    private DictionaryPageResponseDto loadPage(Long dictionaryId, PageRequest pageRequest) {
        Pageable pageableWithSort = pageableWithSort(pageRequest);
        Page<WordIdDto> page = findWordIdsPage(dictionaryId, pageableWithSort);
        List<Long> wordIds = page.stream().map(WordIdDto::getId).toList();
        List<Word> words = loadWordsByFilter(wordIds);

        log.info(
                "Loaded [{}] words from [{}] dictionary for [{}] by [{}] filter",
                words.size(),
                dictionaryId,
                IdentityUtil.getUserId(),
                getPageFilter());

        return mapper.toDto(words, page.getTotalPages(), page.getTotalElements());
    }
}
