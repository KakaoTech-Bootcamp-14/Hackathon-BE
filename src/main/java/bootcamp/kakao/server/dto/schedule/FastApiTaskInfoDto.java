package bootcamp.kakao.server.dto.schedule;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class FastApiTaskInfoDto {
    private Integer taskOrder;
    private String taskTitle;
}
