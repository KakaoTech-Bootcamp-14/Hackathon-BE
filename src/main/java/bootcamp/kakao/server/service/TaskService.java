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

import java.util.List;

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

    @Transactional
    /// LearningSource의 모든 Task를 완료 처리
    public void completeAllTasksByLearningSourceId(Long learningSourceId) {
        List<Task> tasks = taskRepository.findAllByChapter_LearningSourceId(learningSourceId);

        if (tasks.isEmpty()) {
            throw new GeneralException(Code.NOT_FOUND);
        }

        tasks.forEach(task -> task.updateStatus(TaskStatus.DONE));
    }

    @Transactional
    /// Chapter의 모든 Task를 완료 처리
    public void completeAllTasksByChapterId(Long chapterId) {
        List<Task> tasks = taskRepository.findAllByChapterId(chapterId);

        if (tasks.isEmpty()) {
            throw new GeneralException(Code.NOT_FOUND);
        }

        tasks.forEach(task -> task.updateStatus(TaskStatus.DONE));
    }

}
