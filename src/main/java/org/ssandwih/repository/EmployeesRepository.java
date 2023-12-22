package org.ssandwih.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.ssandwih.domain.Employees;

/**
 * Spring Data JPA repository for the Employees entity.
 */
@Repository
public interface EmployeesRepository extends JpaRepository<Employees, Long> {
    default Optional<Employees> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Employees> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Employees> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select employees from Employees employees left join fetch employees.empManagerFk left join fetch employees.empDeptFk left join fetch employees.empJobFk",
        countQuery = "select count(employees) from Employees employees"
    )
    Page<Employees> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select employees from Employees employees left join fetch employees.empManagerFk left join fetch employees.empDeptFk left join fetch employees.empJobFk"
    )
    List<Employees> findAllWithToOneRelationships();

    @Query(
        "select employees from Employees employees left join fetch employees.empManagerFk left join fetch employees.empDeptFk left join fetch employees.empJobFk where employees.id =:id"
    )
    Optional<Employees> findOneWithToOneRelationships(@Param("id") Long id);
}
