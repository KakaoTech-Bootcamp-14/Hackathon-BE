package bootcamp.kakao.server.repository;

import bootcamp.kakao.server.domain.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Slice<Chat> findByLearningSourceId(Long learningSourceId,  Pageable pageable);
}
