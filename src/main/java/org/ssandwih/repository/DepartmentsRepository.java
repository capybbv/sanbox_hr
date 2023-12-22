package org.ssandwih.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.ssandwih.domain.Departments;

/**
 * Spring Data JPA repository for the Departments entity.
 */
@Repository
public interface DepartmentsRepository extends JpaRepository<Departments, Long> {
    default Optional<Departments> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Departments> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Departments> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select departments from Departments departments left join fetch departments.deptLocFk left join fetch departments.depMgrFk",
        countQuery = "select count(departments) from Departments departments"
    )
    Page<Departments> findAllWithToOneRelationships(Pageable pageable);

    @Query("select departments from Departments departments left join fetch departments.deptLocFk left join fetch departments.depMgrFk")
    List<Departments> findAllWithToOneRelationships();

    @Query(
        "select departments from Departments departments left join fetch departments.deptLocFk left join fetch departments.depMgrFk where departments.id =:id"
    )
    Optional<Departments> findOneWithToOneRelationships(@Param("id") Long id);
}
