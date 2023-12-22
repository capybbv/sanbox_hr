package org.ssandwih.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.ssandwih.domain.Countries;

/**
 * Spring Data JPA repository for the Countries entity.
 */
@Repository
public interface CountriesRepository extends JpaRepository<Countries, Long> {
    default Optional<Countries> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Countries> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Countries> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select countries from Countries countries left join fetch countries.countrRegFk",
        countQuery = "select count(countries) from Countries countries"
    )
    Page<Countries> findAllWithToOneRelationships(Pageable pageable);

    @Query("select countries from Countries countries left join fetch countries.countrRegFk")
    List<Countries> findAllWithToOneRelationships();

    @Query("select countries from Countries countries left join fetch countries.countrRegFk where countries.id =:id")
    Optional<Countries> findOneWithToOneRelationships(@Param("id") Long id);
}
