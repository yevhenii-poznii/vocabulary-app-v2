package com.kiskee.vocabulary.service.vocabulary.word.page.impl.asc;

import com.kiskee.vocabulary.enums.vocabulary.PageFilter;
import com.kiskee.vocabulary.mapper.dictionary.DictionaryPageMapper;
import com.kiskee.vocabulary.model.dto.vocabulary.word.WordIdDto;
import com.kiskee.vocabulary.model.entity.vocabulary.Word;
import com.kiskee.vocabulary.repository.vocabulary.DictionaryPageRepository;
import com.kiskee.vocabulary.service.vocabulary.word.page.AbstractDictionaryPageLoaderASC;
import com.kiskee.vocabulary.service.vocabulary.word.page.DictionaryPageLoader;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DictionaryPageLoaderOnlyUseInRepetitionASC extends AbstractDictionaryPageLoaderASC
        implements DictionaryPageLoader {

    @Override
    protected Page<WordIdDto> findWordIdsPage(Long dictionaryId, Pageable pageable) {
        return getRepository().findByDictionaryIdAndUseInRepetition(dictionaryId, true, pageable);
    }

    @Override
    protected List<Word> loadWordsByFilter(List<Long> wordIds) {
        return getRepository().findByIdInAndUseInRepetitionOrderByAddedAtAsc(wordIds, true);
    }

    @Override
    public PageFilter getPageFilter() {
        return PageFilter.ONLY_USE_IN_REPETITION_ASC;
    }

    public DictionaryPageLoaderOnlyUseInRepetitionASC(
            DictionaryPageRepository repository, DictionaryPageMapper mapper) {
        super(repository, mapper);
    }
}
