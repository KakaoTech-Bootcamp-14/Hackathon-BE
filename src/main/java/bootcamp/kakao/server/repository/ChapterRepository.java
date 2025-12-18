package bootcamp.kakao.server.repository;

import bootcamp.kakao.server.domain.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
        void deleteByLearningSourceId(Long learningSourceId);
        List<Chapter> findAllByLearningSourceId(Long learningSourceId);
}