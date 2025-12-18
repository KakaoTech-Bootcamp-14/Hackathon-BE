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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {

    private final UserRepository userRepository;
    private final LearningSourceRepository learningSourceRepository;
    private final StudyPlanRepository studyPlanRepository;
    private final ChapterRepository chapterRepository;
    private final TaskRepository taskRepository;
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
                responseTasks.add(
                        TaskInfoDto.builder()
                                .taskId(task.getId())
                                .taskOrder(fastApiTaskInfoDto.getTaskOrder())
                                .taskTitle(fastApiTaskInfoDto.getTaskTitle())
                                .studyDate(studyDate)
                                .build()
                );
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
                .learningSourceId(learningSource.getId())
                .learningSourceTitle(learningSource.getTitle())
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

        // 6. 기존 chapter 조회
        List<Chapter> oldChapters =
                chapterRepository.findAllByLearningSourceId(learningSourceId);

        // 7. chapter에 속한 task 전부 삭제
        List<Long> chapterIds = oldChapters.stream()
                .map(Chapter::getId)
                .toList();

        taskRepository.deleteByChapterIdIn(chapterIds);

        // 8. chapter 삭제
        chapterRepository.deleteAll(oldChapters);

        // 9. 남은 날짜 계산
        LocalDate newStartDate = LocalDate.now();
        LocalDate newEndDate = createScheduleRequestDto.getEndDate();

        List<LocalDate> studyDates = generateDates(
                newStartDate,
                newEndDate,
                createScheduleRequestDto.isExcludeWeekend()
        );

        int remainingDays = studyDates.size();

        // 10. FastAPI 재스케줄링 요청
        List<FastApiChapterInfoDto> newChapters =
                fastApiClient.rescheduleChapters(
                        learningSourceId,
                        remainingDays,
                        createScheduleRequestDto.getDailyStudyTime(),
                        completedTaskTitles,
                        remainingTaskTitles
                );

        // 11. Chapter + Task 재생성
        List<ChapterInfoDto> responseChapters = new ArrayList<>();

        for (FastApiChapterInfoDto chapterDto : newChapters) {

            LocalDate studyDate = studyDates.get(chapterDto.getChapterOrder() - 1);

            Chapter chapter = Chapter.createChapter(
                    chapterDto.getChapterOrder(),
                    chapterDto.getChapterTitle(),
                    learningSource
            );
            chapterRepository.save(chapter);

            List<TaskInfoDto> responseTasks = new ArrayList<>();

            for (FastApiTaskInfoDto taskDto : chapterDto.getTasks()) {

                Task task = Task.createTask(
                        studyPlan,
                        chapter,
                        studyDate,
                        taskDto.getTaskTitle(),
                        taskDto.getTaskOrder()
                );
                taskRepository.save(task);

                responseTasks.add(
                        TaskInfoDto.builder()
                                .taskId(task.getId())
                                .taskOrder(taskDto.getTaskOrder())
                                .taskTitle(taskDto.getTaskTitle())
                                .studyDate(studyDate)
                                .build()
                );
            }

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
                .learningSourceId(learningSource.getId())
                .learningSourceTitle(learningSource.getTitle())
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
