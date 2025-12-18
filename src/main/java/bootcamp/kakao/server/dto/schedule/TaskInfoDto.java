package bootcamp.kakao.server.dto.schedule;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class TaskInfoDto {
    private Integer taskOrder;
    private String taskTitle;
    private LocalDate studyDate;
}
