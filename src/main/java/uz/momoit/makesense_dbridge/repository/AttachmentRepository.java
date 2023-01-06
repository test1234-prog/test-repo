package uz.momoit.makesense_dbridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.momoit.makesense_dbridge.service.TaskDtlDTO;
import uz.momoit.makesense_dbridge.service.dto.ImageOfTaskResDTO;
import uz.momoit.makesense_dbridge.service.dto.RootEntity;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<RootEntity, Integer> {

    @Query(value = "select t.ATT_SEQ attSeq, t.DTL_SEQ dtlSeq, concat(t.PATH, '/', t.Name) path, t.EXT, t.SIZE, " +
                                                                 "COALESCE((select " +
                                                                    "case " +
                                                                          "when e.VRIFYSTTUS = 1 then 'UNCHECKED' " +
                                                                          "when e.VRIFYSTTUS = 2 then 'APPROVED' " +
                                                                          "when e.VRIFYSTTUS = 3 then 'REJECTED' " +
                                                                     "end "+
                                                                    "from TB_EDU_RESULT e " +
                                                                   "where e.ATT_SEQ = t.att_seq), 'UNDONE') status " +
                     "from TB_ATT t " +
                    "where t.dtl_seq = :dtlSeq ", nativeQuery = true)
    List<ImageOfTaskResDTO> getImagesByTask(Long dtlSeq);

    @Query(value = "select case when count(*) > 0 then true else false end " +
                     "from TB_EDU_RESULT e " +
                    "where e.att_seq = :attSeq", nativeQuery = true)
    Integer checkEduResult(Long attSeq);

    @Transactional
    @Modifying
    @Query(value = "insert into TB_EDU_RESULT(login_id, edu_seq, dtl_seq, att_seq, status, comptpoint, vrifysttus)" +
                    " values(:login_id, :edu_seq, :dtl_seq, :att_seq, :status, :comptpoint, :vrifysttus) ", nativeQuery = true)
    void insertEduResult(String login_id, Long edu_seq, Long dtl_seq, Long att_seq, String status, String comptpoint, String vrifysttus);

    @Query(value = "select t.DTL_SEQ dtlSeq, t.EDU_SEQ eduSeq, t.LOGIN_ID loginId " +
                     "from TB_TASK_DTL t " +
                    "where t.DTL_SEQ in (select a.DTL_SEQ " +
                                          "from TB_ATT a " +
                                         "where a.ATT_SEQ = :attSeq)", nativeQuery = true)
    TaskDtlDTO getTaskDtl(Long attSeq);

    @Query(value = "insert into TB_LABEL_DATA(att_seq,label_name, label_order, bbox_x, bbox_y, bbox_width, bbox_height, img_width, img_height)" +
            " values(:att_seq, :label_name, :label_order, :bbox_x, :bbox_y, :bbox_width, :bbox_height, :img_width, :img_height)", nativeQuery = true)
    void insertLabelData(Long att_seq, String label_name, int label_order, int bbox_x, int bbox_y, int bbox_width, int bbox_height, int img_width, int img_height);

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
    @Query(value = "delete from TB_LABEL_DATA where ATT_SEQ in :ids ", nativeQuery = true)
    void deleteLabelData(@Param("ids")List<Long> ids);

    @Query(value = "update TB_EDU_RESULT " +
                      "set VRIFYSTTUS = :i, QC_ID = :qcId, QC_DT = :now, COMPTPOINT = :point " +
                    "where ATT_SEQ = :attSeq ", nativeQuery = true)
    void updateEduResult(int i, Long attSeq, Long qcId, Long point, LocalDateTime now);


    @Transactional
    @Modifying
    @Query(value = "update TB_TASK_DTL " +
                      "set TASK_DTL_STAT = case " +
                                              "when ((select count(dtl_seq) from TB_ATT where dtl_seq = :dtlSeq) = " +
                                                    "(select count(dtl_seq) from TB_EDU_RESULT where dtl_seq = :dtlSeq and vrifysttus=2)) then 4 " +
                                              "else 5 end " +
                   "where dtl_seq = :dtlSeq", nativeQuery = true)
    void checkApprovedImagesOfTask(int dtlSeq);

    @Transactional
    @Modifying
    @Query(value = "insert into TB_POINT(login_id, nowpoint, lastupdde) values(:loginId,:point, :now) ", nativeQuery = true)
    void updateTbPoint(Long loginId, Long point, LocalDateTime now);

    @Query(value = "select point " +
                     "from TB_EDU_MST " +
                    "where edu_seq = (select edu_seq " +
                                       "from TB_TASK_DTL " +
                                      "where dtl_seq = :taskId)", nativeQuery = true)
    Long getPointByTaskId(Long taskId);
}
