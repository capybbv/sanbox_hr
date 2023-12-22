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
import org.ssandwih.domain.Departments;
import org.ssandwih.repository.DepartmentsRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Departments} entity.
 */
public interface DepartmentsSearchRepository extends ElasticsearchRepository<Departments, Long>, DepartmentsSearchRepositoryInternal {}

interface DepartmentsSearchRepositoryInternal {
    Page<Departments> search(String query, Pageable pageable);

    Page<Departments> search(Query query);

    @Async
    void index(Departments entity);

    @Async
    void deleteFromIndexById(Long id);
}

class DepartmentsSearchRepositoryInternalImpl implements DepartmentsSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final DepartmentsRepository repository;

    DepartmentsSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, DepartmentsRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Departments> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Departments> search(Query query) {
        SearchHits<Departments> searchHits = elasticsearchTemplate.search(query, Departments.class);
        List<Departments> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Departments entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Departments.class);
    }
}
