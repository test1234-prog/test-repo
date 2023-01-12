package uz.momoit.makesense_dbridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.momoit.makesense_dbridge.domain.TaskDtl;

import javax.transaction.Transactional;

@Repository
public interface TaskDtlRepository extends JpaRepository<TaskDtl, Long> {

    @Transactional
    @Modifying
    @Query(value = "update TB_TASK_DTL set TASK_DTL_PROG = " +
            "(100*(select count(*) from TB_EDU_RESULT e where e.DTL_SEQ = :dtl_seq and e.STATUS = 'OK')/" +
            "(select count(t.ATT_SEQ) from TB_ATT t where t.DTL_SEQ = :dtl_seq)) " +
            "where DTL_SEQ = :dtl_seq", nativeQuery = true)
    void updateTaskDtlProg(Long dtl_seq);

    @Transactional
    @Modifying
    @Query(value = "update TB_TASK_DTL set TASK_DTL_PROG = 100 " +
            "where DTL_SEQ = :dtl_seq " +
            "and TASK_DTL_PROG = 100", nativeQuery = true)
    void updateTaskDtlStatus(Long dtl_seq);

    @Transactional
    @Modifying
    @Query(value = "update TB_TASK_DTL " +
            "set TASK_DTL_STAT = case " +
            "when ((select count(dtl_seq) from TB_ATT where dtl_seq = :dtlSeq) = " +
            "(select count(dtl_seq) from TB_EDU_RESULT where dtl_seq = :dtlSeq and vrifysttus=2)) then 4 " +
            "else 5 end " +
            "where dtl_seq = :dtlSeq", nativeQuery = true)
    void checkApprovedImagesOfTask(Long dtlSeq);
}
