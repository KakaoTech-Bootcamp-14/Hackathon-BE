package bootcamp.kakao.server.service;

import bootcamp.kakao.server.client.FastApiClient;
import bootcamp.kakao.server.common.enums.Code;
import bootcamp.kakao.server.common.exception.GeneralException;
import bootcamp.kakao.server.domain.*;
import bootcamp.kakao.server.dto.schedule.*;
import bootcamp.kakao.server.enums.TaskStatus;
import bootcamp.kakao.server.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {

    private final UserRepository userRepository;
    private final LearningSourceRepository learningSourceRepository;
    private final StudyPlanRepository studyPlanRepository;
    private final ChapterRepository chapterRepository;
    private final TaskRepository taskRepository;
    private final SummaryRepository summaryRepository;
    private final FastApiClient fastApiClient;

    public CreateScheduleResponseDto createSchedule(MultipartFile multipartFile, CreateScheduleRequestDto createScheduleRequestDto) {

        // chapterTitle 중복 카운트용
        Map<String, Integer> chapterTitleCountMap = new HashMap<>();

        LocalDate startDate = createScheduleRequestDto.getStartDate();
        LocalDate endDate = createScheduleRequestDto.getEndDate();
        boolean isExcludeWeekend = createScheduleRequestDto.isExcludeWeekend();
        int dailyStudyTime = createScheduleRequestDto.getDailyStudyTime();

        User user = userRepository.findById(1L)
                .orElseThrow(() -> new GeneralException(Code.USER_NOT_FOUND));

        // 1. LearningSource 생성
        LearningSource learningSource = LearningSource.createLearningSource(
                createScheduleRequestDto.getLearningSourceTitle(),
                user
        );
        learningSourceRepository.save(learningSource);

        // 2. StudyPlan 생성
        StudyPlan studyPlan = StudyPlan.createStudyPlan(
                learningSource,
                startDate,
                endDate,
                isExcludeWeekend,
                dailyStudyTime
        );
        studyPlanRepository.save(studyPlan);

        // 3. 학습 일수 계산
        int totalDays = calculateDays(startDate, endDate, isExcludeWeekend);

        // 4. FastAPI 호출
        // 파일, 기타 메타데이터 분리
        fastApiClient.uploadDocumentForRag(multipartFile, learningSource.getId());
        List<FastApiChapterInfoDto> fastApiChapterInfoDtos = fastApiClient.createChapters(totalDays, dailyStudyTime, learningSource.getId());

        // 5. 학습 날짜 리스트 생성
        List<LocalDate> studyDates =
                generateDates(startDate, endDate, isExcludeWeekend);


        // 6. Chapter + Task 저장
        List<ChapterInfoDto> responseChapters = new ArrayList<>();
        for (FastApiChapterInfoDto fastApiChapterInfoDto : fastApiChapterInfoDtos) {

            String originalTitle = fastApiChapterInfoDto.getChapterTitle();

            // 현재 몇 번째인지 (처음이면 0)
            int nextCount = chapterTitleCountMap.getOrDefault(originalTitle, 0) + 1;

            // 제목 생성 (무조건 (1)부터)
            String savedTitle = originalTitle + " (" + nextCount + ")";

            // 카운트 업데이트
            chapterTitleCountMap.put(originalTitle, nextCount);



            // chapterOrder = Day index
            LocalDate studyDate = studyDates.get(fastApiChapterInfoDto.getChapterOrder() - 1);

            Chapter chapter = Chapter.createChapter(
                    fastApiChapterInfoDto.getChapterOrder(),
                    savedTitle,
                    learningSource
            );

            chapterRepository.save(chapter);

            List<TaskInfoDto> responseTasks = new ArrayList<>();

            for (FastApiTaskInfoDto fastApiTaskInfoDto : fastApiChapterInfoDto.getTasks()) {
                Task task = Task.createTask(
                        studyPlan,
                        chapter,
                        studyDate,
                        fastApiTaskInfoDto.getTaskTitle(),
                        fastApiTaskInfoDto.getTaskOrder()
                );
                taskRepository.save(task);

                // 응답용 Task DTO 생성
                responseTasks.add(TaskInfoDto.from(task));
            }

            // 응답용 Chapter DTO 생성
            responseChapters.add(
                    ChapterInfoDto.builder()
                            .chapterId(chapter.getId())
                            .chapterOrder(chapter.getSortOrder())
                            .chapterTitle(chapter.getTitle())
                            .taskInfoDtos(responseTasks)
                            .build()
            );

        }

        return CreateScheduleResponseDto.builder()
                .chapterInfoDtos(responseChapters)
                .build();
    }

    public CreateScheduleResponseDto reCreateSchedule(CreateScheduleRequestDto createScheduleRequestDto) {

        // 1. LearningSource 조회
        LearningSource learningSource = learningSourceRepository.findByTitle(createScheduleRequestDto.getLearningSourceTitle())
                .orElseThrow(() -> new GeneralException(Code.NOT_FOUND));
        Long learningSourceId = learningSource.getId();

        // 2. StudyPlan 조회
        StudyPlan studyPlan = studyPlanRepository.findByLearningSourceId(learningSourceId);

        // 3. StudyPlan 업데이트
        studyPlan.update(
                createScheduleRequestDto.getStartDate(),
                createScheduleRequestDto.getEndDate(),
                createScheduleRequestDto.isExcludeWeekend(),
                createScheduleRequestDto.getDailyStudyTime()
        );

        // 4. 기존 Task 조회
        List<Task> tasks = taskRepository.findAllByStudyPlanId(studyPlan.getId());

        // 5. 완료 / 미완료 task 분리 (FastAPI 전달용)
        List<String> completedTaskTitles = tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .map(Task::getTitle)
                .toList();

        List<String> remainingTaskTitles = tasks.stream()
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .map(Task::getTitle)
                .toList();

        if (remainingTaskTitles.isEmpty()) {
            return null;
        }

        // ===== 🔥 완료된 Task는 유지, 미완료된 Task만 삭제 후 재생성 =====

        // 6. 미완료된 Task만 필터링
        List<Task> incompleteTasks = tasks.stream()
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .toList();

        // 7. 미완료된 Task의 Summary 먼저 삭제 (FK 제약 조건 해결)
        if (!incompleteTasks.isEmpty()) {
            List<Long> incompleteTaskIds = incompleteTasks.stream()
                    .map(Task::getId)
                    .toList();
            List<Summary> summaries = summaryRepository.findAllByTaskIdIn(incompleteTaskIds);
            if (!summaries.isEmpty()) {
                summaryRepository.deleteAll(summaries);
            }

            // 8. 미완료된 Task만 삭제
            taskRepository.deleteAll(incompleteTasks);
        }

        // 9. Task가 하나도 남지 않은 Chapter만 삭제
        List<Chapter> allChapters = chapterRepository.findAllByLearningSourceId(learningSourceId);
        List<Chapter> emptyChapters = new ArrayList<>();

        for (Chapter chapter : allChapters) {
            long remainingTaskCount = taskRepository.countByChapterId(chapter.getId());
            if (remainingTaskCount == 0) {
                emptyChapters.add(chapter);
            }
        }

        if (!emptyChapters.isEmpty()) {
            chapterRepository.deleteAll(emptyChapters);
        }

        // 10. 완료된 Task의 studyDate 조회 (중복 방지용)
        List<Task> completedTasks = tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .toList();

        List<LocalDate> usedDates = completedTasks.stream()
                .map(Task::getStudyDate)
                .distinct()
                .sorted()
                .toList();

        // 11. 새로운 Task를 배치할 날짜 계산 (완료된 Task 날짜 제외)
        // 새롭게 지정된 시작일 기준으로 재배치
        LocalDate newStartDate = createScheduleRequestDto.getStartDate();
        LocalDate newEndDate = createScheduleRequestDto.getEndDate();

        List<LocalDate> allPossibleDates = generateDates(
                newStartDate,
                newEndDate,
                createScheduleRequestDto.isExcludeWeekend()
        );

        // 사용 가능한 날짜 = 전체 날짜 - 이미 사용된 날짜
        List<LocalDate> availableDates = allPossibleDates.stream()
                .filter(date -> !usedDates.contains(date))
                .toList();

        int remainingDays = availableDates.size();

        // 12. FastAPI 재스케줄링 요청 (미완료된 Task만 전달)
        List<FastApiChapterInfoDto> newChapters =
                fastApiClient.rescheduleChapters(
                        learningSourceId,
                        remainingDays,
                        createScheduleRequestDto.getDailyStudyTime(),
                        completedTaskTitles,
                        remainingTaskTitles
                );

        // 13. Chapter + Task 재생성 (사용 가능한 날짜에 배치)

        for (FastApiChapterInfoDto chapterDto : newChapters) {

            LocalDate studyDate = availableDates.get(chapterDto.getChapterOrder() - 1);

            Chapter chapter = Chapter.createChapter(
                    chapterDto.getChapterOrder(),
                    chapterDto.getChapterTitle(),
                    learningSource
            );
            chapterRepository.save(chapter);

            for (FastApiTaskInfoDto taskDto : chapterDto.getTasks()) {

                Task task = Task.createTask(
                        studyPlan,
                        chapter,
                        studyDate,
                        taskDto.getTaskTitle(),
                        taskDto.getTaskOrder()
                );
                taskRepository.save(task);
            }
        }

        // 14. 완료된 Task를 포함한 전체 스케줄 응답 재구성
        List<Chapter> updatedChapters = chapterRepository.findAllByLearningSourceId(learningSourceId);
        Map<Long, List<Task>> tasksByChapter = taskRepository.findAllByChapter_LearningSourceId(learningSourceId)
                .stream()
                .collect(Collectors.groupingBy(task -> task.getChapter().getId()));

        List<ChapterInfoDto> responseChapters = updatedChapters.stream()
                .sorted(Comparator.comparingInt(Chapter::getSortOrder))
                .map(chapter -> ChapterInfoDto.builder()
                        .chapterId(chapter.getId())
                        .chapterOrder(chapter.getSortOrder())
                        .chapterTitle(chapter.getTitle())
                        .taskInfoDtos(
                                tasksByChapter.getOrDefault(chapter.getId(), List.of())
                                        .stream()
                                        .sorted(Comparator
                                                .comparing(Task::getStudyDate)
                                                .thenComparing(Task::getSortOrder))
                                        .map(TaskInfoDto::from)
                                        .toList()
                        )
                        .build())
                .toList();

        return CreateScheduleResponseDto.builder()
                .chapterInfoDtos(responseChapters)
                .build();
    }




    public int calculateDays(LocalDate startDate, LocalDate endDate, boolean excludeWeekend) {

        int days = 0;
        LocalDate date = startDate;

        while (!date.isAfter(endDate)) {

            if (excludeWeekend) {
                DayOfWeek dayOfWeek = date.getDayOfWeek();
                if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                    date = date.plusDays(1);
                    continue;
                }
            }

            days++;
            date = date.plusDays(1);
        }

        return days;
    }

    private List<LocalDate> generateDates(
            LocalDate startDate,
            LocalDate endDate,
            boolean excludeWeekend
    ) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate date = startDate;

        while (!date.isAfter(endDate)) {

            if (excludeWeekend) {
                DayOfWeek dayOfWeek = date.getDayOfWeek();
                if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                    date = date.plusDays(1);
                    continue;
                }
            }

            dates.add(date);
            date = date.plusDays(1);
        }

        return dates;
    }




}
