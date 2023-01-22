package uz.momoit.makesense_dbridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.momoit.makesense_dbridge.service.dto.RootEntity;

import javax.persistence.Tuple;
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
                "left join TB_EDU_RESULT e " +
                       "on  e.ATT_SEQ = t.ATT_SEQ " +
                    "where t.dtl_seq = :dtlSeq " +
                      "and (d.LOGIN_ID = :userId or 'Y' = :qcChk) " +
                      "and ('N'= :qcChk and coalesce(e.VRIFYSTTUS,0) not in (2,3) or 'Y' = :qcChk)", nativeQuery = true) // if qcChk = true return all images, else return only not approved images
    List<Tuple> getImagesByTask(String userId, String qcChk, Long dtlSeq);

    @Query(value = "select att_seq " +
                     "from TB_ATT " +
                    "where dtl_seq = :dtlSeq", nativeQuery = true)
    List<Long> getAttachmentIdsByDtSeq(Long dtlSeq);


}
