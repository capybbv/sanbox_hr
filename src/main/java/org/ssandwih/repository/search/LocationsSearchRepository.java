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
import org.ssandwih.domain.Locations;
import org.ssandwih.repository.LocationsRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Locations} entity.
 */
public interface LocationsSearchRepository extends ElasticsearchRepository<Locations, Long>, LocationsSearchRepositoryInternal {}

interface LocationsSearchRepositoryInternal {
    Page<Locations> search(String query, Pageable pageable);

    Page<Locations> search(Query query);

    @Async
    void index(Locations entity);

    @Async
    void deleteFromIndexById(Long id);
}

class LocationsSearchRepositoryInternalImpl implements LocationsSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final LocationsRepository repository;

    LocationsSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, LocationsRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Locations> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Locations> search(Query query) {
        SearchHits<Locations> searchHits = elasticsearchTemplate.search(query, Locations.class);
        List<Locations> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Locations entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Locations.class);
    }
}
