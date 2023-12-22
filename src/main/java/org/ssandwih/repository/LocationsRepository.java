package org.ssandwih.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.ssandwih.domain.Locations;

/**
 * Spring Data JPA repository for the Locations entity.
 */
@Repository
public interface LocationsRepository extends JpaRepository<Locations, Long> {
    default Optional<Locations> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Locations> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Locations> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select locations from Locations locations left join fetch locations.locCIdFk",
        countQuery = "select count(locations) from Locations locations"
    )
    Page<Locations> findAllWithToOneRelationships(Pageable pageable);

    @Query("select locations from Locations locations left join fetch locations.locCIdFk")
    List<Locations> findAllWithToOneRelationships();

    @Query("select locations from Locations locations left join fetch locations.locCIdFk where locations.id =:id")
    Optional<Locations> findOneWithToOneRelationships(@Param("id") Long id);
}
