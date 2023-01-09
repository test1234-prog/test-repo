package uz.momoit.makesense_dbridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.momoit.makesense_dbridge.domain.LabelHistory;

@Repository
public interface LabelHistoryRepository extends JpaRepository<LabelHistory, Long> {

    @Query(value = "insert " +
                     "into TB_LABEL_DATA_HISTORY(att_seq, label_name, label_order, bbox_x, bbox_y, bbox_width, bbox_height, img_width, img_height, reg_user, reg_dt) " +
                   "select att_seq, label_name, label_order, bbox_x, bbox_y, bbox_width, bbox_height, img_width, img_height, reg_user, reg_dt " +
                     "from TB_LABEL_DATA " +
                    "where ATT_SEQ = :attSeq",nativeQuery = true)
    void saveLabelHistory(Long attSeq);
}
