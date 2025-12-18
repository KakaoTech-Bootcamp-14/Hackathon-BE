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
import java.util.List;

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

            // chapterOrder = Day index
            LocalDate studyDate = studyDates.get(fastApiChapterInfoDto.getChapterOrder() - 1);

            Chapter chapter = Chapter.createChapter(
                    fastApiChapterInfoDto.getChapterOrder(),
                    fastApiChapterInfoDto.getChapterTitle(),
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
                                .taskOrder(fastApiTaskInfoDto.getTaskOrder())
                                .taskTitle(fastApiTaskInfoDto.getTaskTitle())
                                .studyDate(studyDate)
                                .build()
                );
            }

            // 응답용 Chapter DTO 생성
            responseChapters.add(
                    ChapterInfoDto.builder()
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
        LearningSource learningSource = learningSourceRepository.findByTitle(createScheduleRequestDto.getLearningSourceTitle());
        Long learningSourceId = learningSource.getId();

        // 2. StudyPlan 조회
        StudyPlan studyPlan = studyPlanRepository.findByLearningSourceId(learningSourceId);

        // 3. 변경된 StudyPlan 업데이트
        studyPlan.update(createScheduleRequestDto.getStartDate(), createScheduleRequestDto.getEndDate(), createScheduleRequestDto.isExcludeWeekend(), createScheduleRequestDto.getDailyStudyTime());

        // 4. 기존 Task 조회
        List<Task> tasks = taskRepository.findAllByStudyPlanId(studyPlan.getId());

        // 5. 완료/미완료 task 분리
        List<Task> completedTasks = tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .toList();

        List<Task> remainingTasks = tasks.stream()
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .toList();

        if (remainingTasks.isEmpty()) {
            return null; // 재배치할 스케줄 없음
        }

        // 4. 완료된 Task, 남은 Task title만 추출 (FastAPI로 보낼 데이터)
        List<String> completedTaskTitles = completedTasks.stream()
                .map(Task::getTitle)
                .toList();

        List<String> remainingTaskTitles = remainingTasks.stream()
                .map(Task::getTitle)
                .toList();

        // 5. 기존 미완료 Task 삭제
        taskRepository.deleteAll(remainingTasks);

        // 6. 기존 Chapter 중 미완료 부분 제거
        chapterRepository.deleteByLearningSourceId(learningSourceId);

        // 7. 남은 날짜 계산
        LocalDate newStartDate = LocalDate.now();
        LocalDate newEndDate = createScheduleRequestDto.getEndDate();

        List<LocalDate> studyDates = generateDates(
                newStartDate,
                newEndDate,
                createScheduleRequestDto.isExcludeWeekend()
        );

        int remainingDays = studyDates.size();

        // 8. FastAPI 재스케줄링 요청
        List<FastApiChapterInfoDto> newChapters =
                fastApiClient.rescheduleChapters(
                        learningSourceId,
                        remainingDays,
                        createScheduleRequestDto.getDailyStudyTime(),
                        completedTaskTitles,
                        remainingTaskTitles
                );

        // 9. Chapter + Task 재생성
        List<ChapterInfoDto> responseChapters = new ArrayList<>();
        for (FastApiChapterInfoDto chapterDto : newChapters) {

            LocalDate studyDate =
                    studyDates.get(chapterDto.getChapterOrder() - 1);

            // Chapter 엔티티 생성
            Chapter chapter = Chapter.createChapter(
                    chapterDto.getChapterOrder(),
                    chapterDto.getChapterTitle(),
                    studyPlan.getLearningSource()
            );
            chapterRepository.save(chapter);

            // Task 응답 DTO 리스트
            List<TaskInfoDto> responseTasks = new ArrayList<>();
            for (FastApiTaskInfoDto fastApiTaskInfoDto : chapterDto.getTasks()) {

                // Task 엔티티 생성
                Task task = Task.createTask(
                        studyPlan,
                        chapter,
                        studyDate,
                        fastApiTaskInfoDto.getTaskTitle(),
                        fastApiTaskInfoDto.getTaskOrder()
                );
                taskRepository.save(task);

                // 응답용 Task DTO
                responseTasks.add(
                        TaskInfoDto.builder()
                                .taskOrder(fastApiTaskInfoDto.getTaskOrder())
                                .taskTitle(fastApiTaskInfoDto.getTaskTitle())
                                .studyDate(studyDate)
                                .build()
                );
            }

            // 응답용 Chapter DTO
            responseChapters.add(
                    ChapterInfoDto.builder()
                            .taskInfoDtos(responseTasks)
                            .build()
            );
        }

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
