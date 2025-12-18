package bootcamp.kakao.server.repository;

import bootcamp.kakao.server.domain.LearningSource;
import bootcamp.kakao.server.domain.User;
import bootcamp.kakao.server.dto.learningsource.LearningSourceResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningSourceRepository extends JpaRepository<LearningSource, Long> {

    Optional<LearningSource> findByTitle(String title);
    List<LearningSource> findAllByUser(User user);
}
