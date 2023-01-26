package uz.momoit.makesense_dbridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uz.momoit.makesense_dbridge.domain.Label;
import uz.momoit.makesense_dbridge.domain.projection.LabelOrdersProjection;

import java.util.List;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {
    @Query(value = "select * " +
                     "from TB_LABEL_DATA " +
                    "where att_seq = :attSeq", nativeQuery = true)
    List<Label> getLabelsByAttSeq(Long attSeq);

    @Modifying
    @Transactional
    @Query(value = "insert into TB_LABEL_DATA(att_seq,label_name, label_order, bbox_x, bbox_y, bbox_width, bbox_height, img_width, img_height) " +
                   "values(:att_seq, :label_name, :label_order, :bbox_x, :bbox_y, :bbox_width, :bbox_height, :img_width, :img_height)", nativeQuery = true)
    void insertLabelData(Long att_seq, String label_name, Long label_order, int bbox_x, int bbox_y, int bbox_width, int bbox_height, int img_width, int img_height);


    @Transactional
    @Modifying
    @Query(value = "delete " +
                     "from TB_LABEL_DATA " +
                    "where ATT_SEQ in :ids ", nativeQuery = true)
    void deleteLabelData(@Param("ids")List<Long> ids);

    @Query(value = "select l.LABEL_ORDER as labelOrder, l.LABEL_NAME as labelName " +
                     "from TB_LABEL_DATA l " +
                    "where l.ATT_SEQ in (select TB_ATT.ATT_SEQ " +
                                          "from TB_ATT " +
                                         "where DTL_SEQ = (select DTL_SEQ " +
                                                            "from TB_ATT " +
                                                           "where TB_ATT.ATT_SEQ = :attSeq))", nativeQuery = true)
    List<LabelOrdersProjection> getLabelOrderIdByAttSeqAndName(Long attSeq);

    @Query(value = "select distinct l.LABEL_ORDER + 1 as labelOrder, l.LABEL_NAME as labelName " +
                     "from TB_LABEL_DATA l " +
                    "where l.ATT_SEQ in (select TB_ATT.ATT_SEQ " +
                                          "from TB_ATT " +
                                         "where DTL_SEQ = :dtlSeq) " +
                 "order by labelOrder", nativeQuery = true)
    List<LabelOrdersProjection> getLabelOrdersByDtlSeq(Long dtlSeq);


    @Query(value = "select l.LB_SEQ as labelSeq, l.LABEL_NAME as labelName " +
                     "from TB_LABEL_DATA l " +
                    "where l.ATT_SEQ in (select TB_ATT.ATT_SEQ " +
                                          "from TB_ATT " +
                                         "where DTL_SEQ = :dtlSeq)", nativeQuery = true)
    List<LabelOrdersProjection> getLabelDataByDtlSeq(Long dtlSeq);

    @Transactional
    @Modifying
    @Query(value = "update TB_LABEL_DATA " +
                      "set LABEL_ORDER = :labelOrder " +
                    "where LB_SEQ = :lbSeq", nativeQuery = true)
    void updateLabelOrder(Long lbSeq, Long labelOrder);
}
