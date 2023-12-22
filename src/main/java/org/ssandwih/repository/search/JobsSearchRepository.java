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
import org.ssandwih.domain.Jobs;
import org.ssandwih.repository.JobsRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Jobs} entity.
 */
public interface JobsSearchRepository extends ElasticsearchRepository<Jobs, Long>, JobsSearchRepositoryInternal {}

interface JobsSearchRepositoryInternal {
    Page<Jobs> search(String query, Pageable pageable);

    Page<Jobs> search(Query query);

    @Async
    void index(Jobs entity);

    @Async
    void deleteFromIndexById(Long id);
}

class JobsSearchRepositoryInternalImpl implements JobsSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final JobsRepository repository;

    JobsSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, JobsRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Jobs> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Jobs> search(Query query) {
        SearchHits<Jobs> searchHits = elasticsearchTemplate.search(query, Jobs.class);
        List<Jobs> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Jobs entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Jobs.class);
    }
}
