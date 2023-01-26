package uz.momoit.makesense_dbridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uz.momoit.makesense_dbridge.service.dto.RootEntity;
import uz.momoit.makesense_dbridge.domain.projection.TaskDtlProjection;

@Repository
public interface TaskDtlRepository extends JpaRepository<RootEntity, Long> {

    @Transactional
    @Modifying
    @Query(value = "update TB_TASK_DTL " +
                     "set TASK_DTL_PROG = " +
                                 "(100 * (select count(*) " +
                                           "from TB_EDU_RESULT e " +
                                          "where e.DTL_SEQ = :dtl_seq " +
                                            "and e.STATUS = 'OK'" +
                                            "and e.VRIFYSTTUS <> '3')/" +
                                 "(select count(t.ATT_SEQ) from TB_ATT t where t.DTL_SEQ = :dtl_seq)) " +
                   "where DTL_SEQ = :dtl_seq", nativeQuery = true)
    void updateTaskDtlProg(Long dtl_seq);

    @Transactional
    @Modifying
    @Query(value = "update TB_TASK_DTL " +
                      "set TASK_DTL_STAT = case " +
                                               "when TASK_DTL_PROG = 100 then 3 " +
                                                "else 2 " +
                                           "end " +
                    "where DTL_SEQ = :dtl_seq " , nativeQuery = true)
    void updateTaskDtlStatus(Long dtl_seq);

    @Transactional
    @Modifying
    @Query(value = "update TB_TASK_DTL " +
                      "set TASK_DTL_STAT = case " +
                                              "when ((select count(dtl_seq) from TB_ATT where dtl_seq = :dtlSeq) = " +
                                                    "(select count(dtl_seq) from TB_EDU_RESULT where dtl_seq = :dtlSeq and vrifysttus = 2)) then 4 " +
                                              "when ((select count(*) from TB_EDU_RESULT e where e.DTL_SEQ = :dtlSeq and VRIFYSTTUS = 3) > 0) then 5 " +
                                              "else 2 " +
                                           "end " +
                    "where dtl_seq = :dtlSeq", nativeQuery = true)
    void checkApprovedImagesOfTask(Long dtlSeq);

    @Query(value = "select DTL_SEQ dtlSeq, EDU_SEQ eduSeq, LOGIN_ID loginId, TASK_DTL_PROG taskDtlProg, TASK_DTL_STAT taskDtlStat, QC_ID qcId " +
                     "from TB_TASK_DTL " +
                    "where dtl_seq = :dtlSeq", nativeQuery = true)
    TaskDtlProjection getTaskDtlByDtlSeq(Long dtlSeq);

    @Query(value = "select t.DTL_SEQ dtlSeq, t.EDU_SEQ eduSeq, t.LOGIN_ID loginId " +
                     "from TB_TASK_DTL t " +
                    "where t.DTL_SEQ in (select a.DTL_SEQ " +
                                          "from TB_ATT a " +
                                         "where a.ATT_SEQ = :attSeq)", nativeQuery = true)
    TaskDtlProjection getTaskDtl(Long attSeq);
}
