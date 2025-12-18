package bootcamp.kakao.server.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer sortOrder;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_source_id", nullable = false)
    private LearningSource learningSource;

    @Builder
    private Chapter(Integer sortOrder, String title, LearningSource learningSource) {
        this.sortOrder = sortOrder;
        this.title = title;
        this.learningSource = learningSource;
    }

    public static Chapter createChapter(Integer sortOrder, String title, LearningSource learningSource) {
        return Chapter.builder()
                .sortOrder(sortOrder)
                .title(title)
                .learningSource(learningSource)
                .build();
    }

}

