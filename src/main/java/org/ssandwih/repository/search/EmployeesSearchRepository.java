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
import org.ssandwih.domain.Employees;
import org.ssandwih.repository.EmployeesRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Employees} entity.
 */
public interface EmployeesSearchRepository extends ElasticsearchRepository<Employees, Long>, EmployeesSearchRepositoryInternal {}

interface EmployeesSearchRepositoryInternal {
    Page<Employees> search(String query, Pageable pageable);

    Page<Employees> search(Query query);

    @Async
    void index(Employees entity);

    @Async
    void deleteFromIndexById(Long id);
}

class EmployeesSearchRepositoryInternalImpl implements EmployeesSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final EmployeesRepository repository;

    EmployeesSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, EmployeesRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Employees> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Employees> search(Query query) {
        SearchHits<Employees> searchHits = elasticsearchTemplate.search(query, Employees.class);
        List<Employees> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Employees entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Employees.class);
    }
}
