package org.ssandwih.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.ssandwih.domain.Countries;
import org.ssandwih.repository.CountriesRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Countries} entity.
 */
public interface CountriesSearchRepository extends ElasticsearchRepository<Countries, Long>, CountriesSearchRepositoryInternal {}

interface CountriesSearchRepositoryInternal {
    Page<Countries> search(String query, Pageable pageable);

    Page<Countries> search(Query query);

    @Async
    void index(Countries entity);

    @Async
    void deleteFromIndexById(Long id);
}

class CountriesSearchRepositoryInternalImpl implements CountriesSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final CountriesRepository repository;

    CountriesSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, CountriesRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Countries> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Countries> search(Query query) {
        SearchHits<Countries> searchHits = elasticsearchTemplate.search(query, Countries.class);
        List<Countries> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Countries entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Countries.class);
    }
}
