package org.ssandwih.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.ssandwih.domain.JobHistory;

/**
 * Spring Data JPA repository for the JobHistory entity.
 */
@Repository
public interface JobHistoryRepository extends JpaRepository<JobHistory, Long> {
    default Optional<JobHistory> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<JobHistory> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<JobHistory> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select jobHistory from JobHistory jobHistory left join fetch jobHistory.jhistDepFk left join fetch jobHistory.jhistJob",
        countQuery = "select count(jobHistory) from JobHistory jobHistory"
    )
    Page<JobHistory> findAllWithToOneRelationships(Pageable pageable);

    @Query("select jobHistory from JobHistory jobHistory left join fetch jobHistory.jhistDepFk left join fetch jobHistory.jhistJob")
    List<JobHistory> findAllWithToOneRelationships();

    @Query(
        "select jobHistory from JobHistory jobHistory left join fetch jobHistory.jhistDepFk left join fetch jobHistory.jhistJob where jobHistory.id =:id"
    )
    Optional<JobHistory> findOneWithToOneRelationships(@Param("id") Long id);
}
