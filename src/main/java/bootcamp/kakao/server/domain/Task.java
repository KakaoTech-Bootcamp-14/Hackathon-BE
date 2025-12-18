package bootcamp.kakao.server.domain;

import bootcamp.kakao.server.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_plan_id", nullable = false)
    private StudyPlan studyPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @Column(nullable = false)
    private LocalDate studyDate;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer sortOrder;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Builder
    private Task(
            StudyPlan studyPlan,
            Chapter chapter,
            LocalDate studyDate,
            String title,
            Integer sortOrder,
            TaskStatus status
    ) {
        this.studyPlan = studyPlan;
        this.chapter = chapter;
        this.studyDate = studyDate;
        this.title = title;
        this.sortOrder = sortOrder;
        this.status = status;
    }

    public static Task createTask(
            StudyPlan studyPlan,
            Chapter chapter,
            LocalDate studyDate,
            String title,
            Integer sortOrder
    ) {
        return Task.builder()
                .studyPlan(studyPlan)
                .chapter(chapter)
                .studyDate(studyDate)
                .title(title)
                .sortOrder(sortOrder)
                .status(TaskStatus.TODO)
                .build();
    }

    /// 상태 변화 메서드 생성
    public void updateStatus(TaskStatus status) {
        this.status = status;
    }
}
