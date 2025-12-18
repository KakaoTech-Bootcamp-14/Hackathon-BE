package bootcamp.kakao.server.repository;

import bootcamp.kakao.server.domain.StudyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyPlanRepository extends JpaRepository<StudyPlan, Long> {
    StudyPlan findByLearningSourceId(Long learningSourceId);
}
