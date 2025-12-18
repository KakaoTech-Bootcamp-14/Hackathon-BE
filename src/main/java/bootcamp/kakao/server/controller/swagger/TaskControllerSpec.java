package bootcamp.kakao.server.controller.swagger;

import bootcamp.kakao.server.common.dto.ResponseDto;
import bootcamp.kakao.server.dto.task.TaskCompletionRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Task API", description = "Task 완료 상태 업데이트 API")
public interface TaskControllerSpec {

    @Operation(
            summary = "Task 상태 업데이트",
            description = "Task 상태를 SUCCESS 또는 CANCEL로 동기화합니다."
    )
    ResponseDto updateTaskCompletionStatus(
            @Parameter(description = "Task ID", example = "1")
            @PathVariable("taskId") Long taskId,

            @Parameter(
                    description = "상태 변경 요청",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskCompletionRequestDto.class))
            )
            @RequestBody TaskCompletionRequestDto request
    );
}
