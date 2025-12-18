package bootcamp.kakao.server.dto.schedule;

import bootcamp.kakao.server.domain.Task;
import bootcamp.kakao.server.enums.TaskStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class TaskInfoDto {
    private Long taskId;
    private Integer taskOrder;
    private String taskTitle;
    private LocalDate studyDate;
    private TaskStatus taskStatus;

    public static TaskInfoDto from(Task task) {
        return TaskInfoDto.builder()
                .taskId(task.getId())
                .taskOrder(task.getSortOrder())
                .taskTitle(task.getTitle())
                .studyDate(task.getStudyDate())
                .taskStatus(task.getStatus())
                .build();
    }
}
