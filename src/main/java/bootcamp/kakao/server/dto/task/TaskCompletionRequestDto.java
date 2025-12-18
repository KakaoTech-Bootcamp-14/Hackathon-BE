package bootcamp.kakao.server.dto.task;

import bootcamp.kakao.server.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskCompletionRequestDto {

    private TaskStatus status;
}
