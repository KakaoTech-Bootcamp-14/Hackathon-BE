package bootcamp.kakao.server.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Summary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false, unique = true)
    private Task task;

    @Lob
    @Column(nullable = false)
    private String content;

    @Builder
    private Summary(Task task, String content) {
        this.task = task;
        this.content = content;
    }

    public static Summary createSummary(Task task, String content) {
        return Summary.builder()
                .task(task)
                .content(content)
                .build();
    }
}

