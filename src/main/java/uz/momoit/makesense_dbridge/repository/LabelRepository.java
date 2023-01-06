package uz.momoit.makesense_dbridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.momoit.makesense_dbridge.domain.Label;

import java.util.List;

public interface LabelRepository extends JpaRepository<Label, Long> {
    @Query(value = "select * " +
                     "from TB_LABEL_DATA " +
                    "where att_seq = :attSeq", nativeQuery = true)
    List<Label> getLabelsByAttSeq(Long attSeq);
}
