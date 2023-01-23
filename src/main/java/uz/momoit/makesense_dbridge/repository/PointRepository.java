package uz.momoit.makesense_dbridge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.momoit.makesense_dbridge.service.dto.RootEntity;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Repository
public interface PointRepository extends JpaRepository<RootEntity, Integer> {

    @Transactional
    @Modifying
    @Query(value = "insert into TB_POINT(login_id, nowpoint, lastupdde) values(:loginId,:point, :now) ", nativeQuery = true)
    void updateTbPoint(String loginId, Long point, LocalDateTime now);
}
