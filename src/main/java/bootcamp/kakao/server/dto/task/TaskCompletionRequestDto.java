package bootcamp.kakao.server.dto.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskCompletionRequestDto {

    private TaskCompletionStatus status;
}
