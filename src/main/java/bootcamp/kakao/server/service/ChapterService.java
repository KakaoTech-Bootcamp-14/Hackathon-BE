package bootcamp.kakao.server.service;

import bootcamp.kakao.server.common.enums.Code;
import bootcamp.kakao.server.common.exception.GeneralException;
import bootcamp.kakao.server.domain.Task;
import bootcamp.kakao.server.dto.learningsource.ProgressResponseDto;
import bootcamp.kakao.server.enums.TaskStatus;
import bootcamp.kakao.server.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChapterService {

    private final TaskRepository taskRepository;

    @Transactional(readOnly = true)
    public ProgressResponseDto getChapterProgress(Long chapterId) {
        long total = taskRepository.countByChapterId(chapterId);
        long done = taskRepository.countByChapterIdAndStatus(chapterId, TaskStatus.DONE);
        int progressRate = (total == 0) ? 0 : (int) ((done * 100L) / total);

        return ProgressResponseDto.builder()
                .totalTaskCount(total)
                .doneTaskCount(done)
                .progressRate(progressRate)
                .build();
    }

    @Transactional
    public void completeAllTasksByChapterId(Long chapterId) {
        List<Task> tasks = taskRepository.findAllByChapterId(chapterId);

        if (tasks.isEmpty()) {
            throw new GeneralException(Code.NOT_FOUND);
        }

        tasks.forEach(task -> task.updateStatus(TaskStatus.DONE));
    }
}
