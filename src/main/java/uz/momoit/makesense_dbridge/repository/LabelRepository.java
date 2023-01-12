package uz.momoit.makesense_dbridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.momoit.makesense_dbridge.domain.Label;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {
    @Query(value = "select * " +
                     "from TB_LABEL_DATA " +
                    "where att_seq = :attSeq", nativeQuery = true)
    List<Label> getLabelsByAttSeq(Long attSeq);

    @Transactional
    @Modifying
    @Query(value = "delete from TB_LABEL_DATA where ATT_SEQ in :ids ", nativeQuery = true)
    void deleteLabelData(@Param("ids")List<Long> ids);
}
