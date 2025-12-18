package bootcamp.kakao.server.service;

import bootcamp.kakao.server.common.enums.Code;
import bootcamp.kakao.server.common.exception.GeneralException;
import bootcamp.kakao.server.domain.Chapter;
import bootcamp.kakao.server.domain.LearningSource;
import bootcamp.kakao.server.domain.Task;
import bootcamp.kakao.server.domain.User;
import bootcamp.kakao.server.dto.home.HomeResponseDto;
import bootcamp.kakao.server.dto.learningsource.LearningSourceResponseDto;
import bootcamp.kakao.server.dto.schedule.ChapterInfoDto;
import bootcamp.kakao.server.dto.schedule.TaskInfoDto;
import bootcamp.kakao.server.repository.ChapterRepository;
import bootcamp.kakao.server.repository.LearningSourceRepository;
import bootcamp.kakao.server.repository.TaskRepository;
import bootcamp.kakao.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final UserRepository userRepository;
    private final LearningSourceRepository learningSourceRepository;
    private final ChapterRepository chapterRepository;
    private final TaskRepository taskRepository;

    @Transactional(readOnly = true)
    public HomeResponseDto getHomeData(Long userId) {

        ///  !TODO 개발 편의성을 위해 현재는 1로 고정
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new GeneralException(Code.NOT_FOUND));

        // 1. LearningSource 조회
        List<LearningSource> learningSources =
                learningSourceRepository.findAllByUser(user);

        if (learningSources.isEmpty()) {
            return HomeResponseDto.builder()
                    .userId(user.getId())
                    .learningSourceResponseDtos(List.of())
                    .build();
        }

        List<Long> learningSourceIds = learningSources.stream()
                .map(LearningSource::getId)
                .toList();

        // 2. Chapter 조회
        List<Chapter> chapters =
                chapterRepository.findAllByLearningSourceIdIn(learningSourceIds);

        List<Long> chapterIds = chapters.stream()
                .map(Chapter::getId)
                .toList();

        // 3. Task 조회
        List<Task> tasks = chapterIds.isEmpty()
                ? List.of()
                : taskRepository.findAllByChapterIdIn(chapterIds);

        // 4. Task → Chapter 기준으로 그룹핑
        Map<Long, List<Task>> taskMap =
                tasks.stream().collect(Collectors.groupingBy(
                        task -> task.getChapter().getId()
                ));

        // 5. Chapter → LearningSource 기준으로 그룹핑
        Map<Long, List<Chapter>> chapterMap =
                chapters.stream().collect(Collectors.groupingBy(
                        chapter -> chapter.getLearningSource().getId()
                ));

        // 6. DTO 조립
        List<LearningSourceResponseDto> learningSourceResponseDtos =
                learningSources.stream()
                        .map(learningSource -> toLearningSourceDto(learningSource, chapterMap, taskMap))
                        .toList();

        return HomeResponseDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .learningSourceResponseDtos(learningSourceResponseDtos)
                .build();

    }

    private LearningSourceResponseDto toLearningSourceDto(
            LearningSource learningSource,
            Map<Long, List<Chapter>> chapterMap,
            Map<Long, List<Task>> taskMap
    ) {
        List<ChapterInfoDto> chapterDtos =
                chapterMap.getOrDefault(learningSource.getId(), List.of())
                        .stream()
                        .sorted(Comparator.comparingInt(Chapter::getSortOrder))
                        .map(chapter -> ChapterInfoDto.builder()
                                .chapterId(chapter.getId())
                                .chapterOrder(chapter.getSortOrder())
                                .chapterTitle(chapter.getTitle())
                                .taskInfoDtos(
                                        taskMap.getOrDefault(chapter.getId(), List.of())
                                                .stream()
                                                .map(TaskInfoDto::from)
                                                .toList()
                                )
                                .build()
                        )
                        .toList();

        return LearningSourceResponseDto.builder()
                .learningSourceId(learningSource.getId())
                .learningSourceTitle(learningSource.getTitle())
                .chapterInfoDtos(chapterDtos)
                .build();
    }

}
