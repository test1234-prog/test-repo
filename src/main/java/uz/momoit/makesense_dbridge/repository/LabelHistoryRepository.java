package uz.momoit.makesense_dbridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.momoit.makesense_dbridge.domain.LabelHistory;

@Repository
public interface LabelHistoryRepository extends JpaRepository<LabelHistory, Long> {
}
