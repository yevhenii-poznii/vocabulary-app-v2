package com.kiskee.vocabulary.service.vocabulary.word.page.impl.asc;

import com.kiskee.vocabulary.enums.vocabulary.PageFilter;
import com.kiskee.vocabulary.mapper.dictionary.DictionaryPageMapper;
import com.kiskee.vocabulary.model.entity.vocabulary.Word;
import com.kiskee.vocabulary.repository.vocabulary.DictionaryPageRepository;
import com.kiskee.vocabulary.service.vocabulary.word.page.AbstractDictionaryPageLoaderASC;
import com.kiskee.vocabulary.service.vocabulary.word.page.DictionaryPageLoader;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DictionaryPageLoaderAllASC extends AbstractDictionaryPageLoaderASC implements DictionaryPageLoader {

    @Override
    protected List<Word> loadWordsByFilter(List<Long> wordIds) {
        return getRepository().findByIdInOrderByAddedAtAsc(wordIds);
    }

    @Override
    public PageFilter getPageFilter() {
        return PageFilter.BY_ADDED_AT_ASC;
    }

    public DictionaryPageLoaderAllASC(DictionaryPageRepository repository, DictionaryPageMapper mapper) {
        super(repository, mapper);
    }
}
