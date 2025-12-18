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
    List<Task> findAllByChapterId(Long chapterId);
    List<Task> findAllByChapterIdIn(List<Long> chapterIds);
    List<Task> findAllByChapter_LearningSourceId(Long learningSourceId);
    void deleteByChapterIdIn(List<Long> chapterIds);

    long countByChapterId(Long chapterId);
    long countByChapterIdAndStatus(Long chapterId, TaskStatus status);
    long countByChapter_LearningSourceId(Long learningSourceId);
    long countByChapter_LearningSourceIdAndStatus(Long learningSourceId, TaskStatus status);
    long countByChapter_LearningSource_UserId(Long userId);
    long countByChapter_LearningSource_UserIdAndStatus(Long userId, TaskStatus status);


}