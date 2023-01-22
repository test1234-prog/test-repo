package uz.momoit.makesense_dbridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.momoit.makesense_dbridge.service.dto.RootEntity;

@Repository
public interface EduMstRepository extends JpaRepository<RootEntity, Long> {

    @Query(value = "select point " +
                     "from TB_EDU_MST " +
                    "where edu_seq = (select edu_seq " +
                                       "from TB_TASK_DTL " +
                                      "where dtl_seq = :taskId)", nativeQuery = true)
    Long getPointByTaskId(Long taskId);
}
