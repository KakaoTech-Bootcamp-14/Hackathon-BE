package bootcamp.kakao.server.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_study_plan_learning_source",
                        columnNames = "learning_source_id"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_source_id", nullable = false)
    private LearningSource learningSource;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private boolean excludeWeekends;

    @Column(nullable = false)
    private int studyTime;

    @Builder
    private StudyPlan(
            LearningSource learningSource,
            LocalDate startDate,
            LocalDate endDate,
            boolean excludeWeekends,
            int studyTime
    ) {
        this.learningSource = learningSource;
        this.startDate = startDate;
        this.endDate = endDate;
        this.excludeWeekends = excludeWeekends;
        this.studyTime = studyTime;
    }

    public static StudyPlan createStudyPlan(
            LearningSource learningSource,
            LocalDate startDate,
            LocalDate endDate,
            boolean excludeWeekends,
            int studyTime
    ) {
        return StudyPlan.builder()
                .learningSource(learningSource)
                .startDate(startDate)
                .endDate(endDate)
                .excludeWeekends(excludeWeekends)
                .studyTime(studyTime)
                .build();
    }

    public void update(LocalDate startDate, LocalDate endDate, boolean excludeWeekends, int studyTime) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.excludeWeekends = excludeWeekends;
        this.studyTime = studyTime;

    }
}

