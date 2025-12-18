package bootcamp.kakao.server.repository;

import bootcamp.kakao.server.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByStudyPlanId(Long studyPlanId);
}