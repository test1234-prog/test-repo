package uz.momoit.makesense_dbridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uz.momoit.makesense_dbridge.service.dto.RootEntity;

@Repository
public interface EduResultHistoryRepository extends JpaRepository<RootEntity, Long> {


    @Modifying
    @Transactional
    @Query(value = "insert " +
                     "into TB_EDU_RESULT_HISTORY(LOGIN_ID, EDU_SEQ, DTL_SEQ, ATT_SEQ, STATUS, COMPTPOINT, VRIFYSTTUS, QC_ID, QC_DT, REG_USER, REG_DT) " +
                   "select LOGIN_ID, EDU_SEQ, DTL_SEQ, ATT_SEQ, STATUS, COMPTPOINT, VRIFYSTTUS, QC_ID, QC_DT, REG_USER, REG_DT " +
                     "from TB_EDU_RESULT " +
                    "where ATT_SEQ = :attSeq", nativeQuery = true)
    void savedEduResultHistory(Long attSeq);
}
