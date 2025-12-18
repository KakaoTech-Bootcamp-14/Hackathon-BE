package bootcamp.kakao.server.controller;

import bootcamp.kakao.server.common.dto.ResponseDto;
import bootcamp.kakao.server.common.enums.Code;
import bootcamp.kakao.server.controller.swagger.TaskControllerSpec;
import bootcamp.kakao.server.dto.task.TaskCompletionRequestDto;
import bootcamp.kakao.server.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController implements TaskControllerSpec {

    /// 서비스 주입
    private final TaskService taskService;

    @PatchMapping("/{taskId}/completion")
    public ResponseDto updateTaskCompletionStatus(
            @PathVariable("taskId") Long taskId,
            @RequestBody TaskCompletionRequestDto request
    ) {
        taskService.updateCompletionStatus(taskId, request.getStatus());
        return new ResponseDto(Code.OK.getCode(), "Task 상태가 성공적으로 업데이트되었습니다.");
    }
}
