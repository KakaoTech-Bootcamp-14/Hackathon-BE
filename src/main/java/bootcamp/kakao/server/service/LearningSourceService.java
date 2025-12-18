package bootcamp.kakao.server.service;

import bootcamp.kakao.server.client.FastApiClient;
import bootcamp.kakao.server.common.enums.Code;
import bootcamp.kakao.server.common.exception.GeneralException;
import bootcamp.kakao.server.domain.Chapter;
import bootcamp.kakao.server.domain.LearningSource;
import bootcamp.kakao.server.domain.Summary;
import bootcamp.kakao.server.domain.Task;
import bootcamp.kakao.server.dto.learningsource.LearningSourceResponseDto;
import bootcamp.kakao.server.dto.learningsource.LearningSourceSummaryResponseDto;
import bootcamp.kakao.server.dto.schedule.ChapterInfoDto;
import bootcamp.kakao.server.dto.schedule.TaskInfoDto;
import bootcamp.kakao.server.repository.ChapterRepository;
import bootcamp.kakao.server.repository.LearningSourceRepository;
import bootcamp.kakao.server.repository.SummaryRepository;
import bootcamp.kakao.server.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class LearningSourceService {

    private final LearningSourceRepository learningSourceRepository;
    private final ChapterRepository chapterRepository;
    private final TaskRepository taskRepository;
    private final FastApiClient fastApiClient;
    private final SummaryRepository summaryRepository;

    public LearningSourceResponseDto getLearningSourceDetails(Long learningSourceId) {

        // 1. LearningSource title 가져오기
        LearningSource learningSource = learningSourceRepository.findById(learningSourceId)
                .orElseThrow(() -> new GeneralException(Code.NOT_FOUND));

        // 2. LearningSource chapter, task 목록 가져오기
        List<Chapter> chapters = chapterRepository.findAllByLearningSourceId(learningSourceId);
        List<Long> chapterIds = chapters.stream()
                .map(Chapter::getId)
                .toList();

        List<Task> tasks = taskRepository.findAllByChapterIdIn(chapterIds);

        Map<Long, List<Task>> tasKMap = tasks.stream()
                .collect(Collectors.groupingBy(
                        task -> task.getChapter().getId()
                ));

        List<ChapterInfoDto> chapterInfoDtos = chapters.stream()
                .map(chapter -> ChapterInfoDto.builder()
                        .chapterId(chapter.getId())
                        .chapterOrder(chapter.getSortOrder())
                        .chapterTitle(chapter.getTitle())
                        .taskInfoDtos(
                                tasKMap.getOrDefault(chapter.getId(), List.of())
                                        .stream()
                                        .map(TaskInfoDto::from)
                                        .toList()
                        )
                        .build()
                )
                .toList();

        return LearningSourceResponseDto.builder()
                .learningSourceTitle(learningSource.getTitle())
                .chapterInfoDtos(chapterInfoDtos)
                .build();
    }

    public LearningSourceSummaryResponseDto getLearningSourceSummary(Long learningSourceId, Long taskId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new GeneralException(Code.NOT_FOUND));

        LearningSourceSummaryResponseDto learningSourceSummaryResponseDto = fastApiClient.getLearningSourceSummary(learningSourceId, task.getTitle());

        Summary summary = Summary.createSummary(task,
                learningSourceSummaryResponseDto.getContent());
        summaryRepository.save(summary);

        return learningSourceSummaryResponseDto;
    }
}
