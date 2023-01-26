package uz.momoit.makesense_dbridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uz.momoit.makesense_dbridge.service.dto.RootEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EduResultRepository extends JpaRepository<RootEntity, Long> {

    @Query(value = "select case when count(*) > 0 then true else false end " +
                     "from TB_EDU_RESULT e " +
                    "where e.att_seq = :attSeq", nativeQuery = true)
    Integer checkEduResult(Long attSeq);

    @Transactional
    @Modifying
    @Query(value = "insert into TB_EDU_RESULT(login_id, edu_seq, dtl_seq, att_seq, status, comptpoint, vrifysttus) " +
                   "values(:login_id, :edu_seq, :dtl_seq, :att_seq, :status, :comptpoint, :vrifysttus) ", nativeQuery = true)
    void insertEduResult(String login_id, Long edu_seq, Long dtl_seq, Long att_seq, String status, String comptpoint, String vrifysttus);


    @Transactional
    @Modifying
    @Query(value = "update TB_EDU_RESULT " +
                      "set VRIFYSTTUS = :i, QC_ID = :qcId, QC_DT = :now, COMPTPOINT = :point " +
                    "where ATT_SEQ = :attSeq ", nativeQuery = true)
    void updateEduResult(int i, Long attSeq, String qcId, Long point, LocalDateTime now);

    @Query(value = "select VRIFYSTTUS " +
                     "from TB_EDU_RESULT " +
                    "where ATT_SEQ = :attSeq", nativeQuery = true)
    String getVrifySttusByAttSeq(Long attSeq);

    @Query(value = "select count(*) " +
                     "from TB_EDU_RESULT " +
                    "where ATT_SEQ in :attSeqId " +
                      "and VRIFYSTTUS = 2", nativeQuery = true)
    Long checkExistsApprovedImage(@Param("attSeqId") List<Long> attSeqId);

}
