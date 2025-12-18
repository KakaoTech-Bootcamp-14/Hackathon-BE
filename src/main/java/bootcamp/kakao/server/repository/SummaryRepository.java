package bootcamp.kakao.server.repository;

import bootcamp.kakao.server.domain.Summary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SummaryRepository extends JpaRepository<Summary, Long> {
    List<Summary> findAllByTaskIdIn(List<Long> taskIds);
}
