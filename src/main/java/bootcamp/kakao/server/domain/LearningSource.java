package bootcamp.kakao.server.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LearningSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    private LearningSource(String title, User user) {
        this.title = title;
        this.user = user;
    }

    public static LearningSource createLearningSource(String title, User user) {
        return LearningSource.builder()
                .title(title)
                .user(user)
                .build();
    }
}

