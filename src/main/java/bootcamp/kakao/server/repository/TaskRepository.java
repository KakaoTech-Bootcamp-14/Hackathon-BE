package bootcamp.kakao.server.repository;

import bootcamp.kakao.server.domain.Chapter;
import bootcamp.kakao.server.domain.Task;
import bootcamp.kakao.server.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByStudyPlanId(Long studyPlanId);
    List<Task> findAllByChapterIdIn(List<Long> chapterIds);
    void deleteByChapterIdIn(List<Long> chapterIds);

//    long countByLearningSourceId(Long learningSourceId);
//    long countByLearningSourceIdAndStatus(Long learningSourceId, TaskStatus status);
//    long countByLearningSourceUserId(Long userId);
//    long countByLearningSourceUserIdAndStatus(Long userId, TaskStatus status);


}