package uz.momoit.makesense_dbridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.momoit.makesense_dbridge.domain.EduResult;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Repository
public interface EduResultRepository extends JpaRepository<EduResult, Long> {

    @Query(value = "select case when count(*) > 0 then true else false end " +
                     "from TB_EDU_RESULT e " +
                    "where e.att_seq = :attSeq", nativeQuery = true)
    Integer checkEduResult(Long attSeq);

    @Transactional
    @Modifying
    @Query(value = "insert into TB_EDU_RESULT(login_id, edu_seq, dtl_seq, att_seq, status, comptpoint, vrifysttus) " +
                   "values(:login_id, :edu_seq, :dtl_seq, :att_seq, :status, :comptpoint, :vrifysttus) ", nativeQuery = true)
    void insertEduResult(String login_id, Long edu_seq, Long dtl_seq, Long att_seq, String status, String comptpoint, String vrifysttus);


    @Query(value = "update TB_EDU_RESULT " +
                      "set VRIFYSTTUS = :i, QC_ID = :qcId, QC_DT = :now, COMPTPOINT = :point " +
                    "where ATT_SEQ = :attSeq ", nativeQuery = true)
    void updateEduResult(int i, Long attSeq, Long qcId, Long point, LocalDateTime now);

    @Query(value = "select VRIFYSTTUS " +
                     "from TB_EDU_RESULT " +
                    "where ATT_SEQ = :attSeq", nativeQuery = true)
    String getVrifySttusByAttSeq(Long attSeq);

}
