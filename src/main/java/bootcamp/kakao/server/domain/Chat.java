package bootcamp.kakao.server.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChatRole role;

    @Lob
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_source_id", nullable = false)
    private LearningSource learningSource;

    @Builder
    private Chat(ChatRole role, String content, LearningSource learningSource) {
        this.role = role;
        this.content = content;
        this.learningSource = learningSource;
    }

    public static Chat createUserMessage(LearningSource learningSource, String content) {
        return Chat.builder()
                .role(ChatRole.USER)
                .content(content)
                .learningSource(learningSource)
                .build();
    }

    public static Chat createAssistantMessage(LearningSource learningSource, String content) {
        return Chat.builder()
                .role(ChatRole.ASSISTANT)
                .content(content)
                .learningSource(learningSource)
                .build();
    }
}
