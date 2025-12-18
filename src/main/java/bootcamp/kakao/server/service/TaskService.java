package bootcamp.kakao.server.service;

import bootcamp.kakao.server.common.enums.Code;
import bootcamp.kakao.server.common.exception.GeneralException;
import bootcamp.kakao.server.domain.Task;
import bootcamp.kakao.server.dto.task.TaskCompletionStatus;
import bootcamp.kakao.server.enums.TaskStatus;
import bootcamp.kakao.server.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    @Transactional
    /// 더티체킹으로 DB 호출 없이 저장
    public void updateCompletionStatus(Long taskId, TaskCompletionStatus status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new GeneralException(Code.NOT_FOUND));

        if (status == null) {
            throw new GeneralException(Code.INVALID_INPUT_VALUE);
        }

        TaskStatus nextStatus = status == TaskCompletionStatus.SUCCESS
                ? TaskStatus.DONE
                : TaskStatus.TODO;

        task.updateStatus(nextStatus);
    }

}
