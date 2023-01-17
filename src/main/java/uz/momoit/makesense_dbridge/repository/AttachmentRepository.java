package uz.momoit.makesense_dbridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.momoit.makesense_dbridge.service.dto.TaskDtlDTO;
import uz.momoit.makesense_dbridge.service.dto.ImageOfTaskResDTO;
import uz.momoit.makesense_dbridge.service.dto.RootEntity;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<RootEntity, Integer> {

    @Query(value = "select t.ATT_SEQ attSeq, t.DTL_SEQ dtlSeq, t.NAME, concat(t.PATH, '/', t.Name) path, t.EXT, t.SIZE, " +
                                                                 "COALESCE((select " +
                                                                    "case " +
                                                                          "when e.VRIFYSTTUS = 1 then 'UNCHECKED' " +
                                                                          "when e.VRIFYSTTUS = 2 then 'APPROVED' " +
                                                                          "when e.VRIFYSTTUS = 3 then 'REJECTED' " +
                                                                     "end "+
                                                                    "from TB_EDU_RESULT e " +
                                                                   "where e.ATT_SEQ = t.ATT_SEQ), 'UNDONE') status " +
                     "from TB_ATT t " +
                     "join TB_TASK_DTL d " +
                       "on t.DTL_SEQ = d.DTL_SEQ " +
                    "where t.dtl_seq = :dtlSeq " +
                      "and d.LOGIN_ID = :userId", nativeQuery = true)
    List<ImageOfTaskResDTO> getImagesByTask(String userId, Long dtlSeq);



    @Query(value = "select t.DTL_SEQ dtlSeq, t.EDU_SEQ eduSeq, t.LOGIN_ID loginId " +
                     "from TB_TASK_DTL t " +
                    "where t.DTL_SEQ in (select a.DTL_SEQ " +
                                          "from TB_ATT a " +
                                         "where a.ATT_SEQ = :attSeq)", nativeQuery = true)
    TaskDtlDTO getTaskDtl(Long attSeq);

    @Transactional
    @Modifying
    @Query(value = "insert into TB_POINT(login_id, nowpoint, lastupdde) values(:loginId,:point, :now) ", nativeQuery = true)
    void updateTbPoint(String loginId, Long point, LocalDateTime now);

    @Query(value = "select point " +
                     "from TB_EDU_MST " +
                    "where edu_seq = (select edu_seq " +
                                       "from TB_TASK_DTL " +
                                      "where dtl_seq = :taskId)", nativeQuery = true)
    Long getPointByTaskId(Long taskId);

    @Query(value = "select att_seq " +
                     "from TB_ATT " +
                    "where dtl_seq = :dtlSeq", nativeQuery = true)
    List<Long> getAttachmentIdsByDtSeq(Long dtlSeq);


}
