package bootcamp.kakao.server.client;

import bootcamp.kakao.server.dto.schedule.FastApiChapterInfoDto;
import bootcamp.kakao.server.dto.schedule.ReCreateScheduleRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FastApiClient {

    private final WebClient webClient;

    @Value("${fastapi.base-url}")
    private String fastApiBaseUrl;

    /**
     * PDF를 RAG 문서로 등록
     */
    public void uploadDocumentForRag(MultipartFile multipartFile, Long learningSourceId) {

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        Resource resource = multipartFile.getResource();
        body.add("file", resource);
        body.add("study_session_id", learningSourceId);

        webClient.post()
                .uri(fastApiBaseUrl + "/documents/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(body))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    /**
     * PDF + 학습 기간 기반 Chapter/Task 생성 요청
     */
    public List<FastApiChapterInfoDto> createChapters(
            int totalDays,
            int dailyStudyTime,
            Long learningSourceId
    ) {
        Map<String, Object> body = Map.of(
                "total_days", totalDays,
                "hours_per_day", dailyStudyTime,
                "study_session_id", learningSourceId.toString()
        );

        return webClient.post()
                .uri(fastApiBaseUrl + "/study/plan")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(FastApiChapterInfoDto.class)
                .collectList()
                .block();
    }

    public List<FastApiChapterInfoDto> rescheduleChapters(
            Long learningSourceId,
            int remainingDays,
            int dailyStudyTime,
            List<String> completedTaskTitles,
            List<String> remainingTaskTitles
    ) {

        ReCreateScheduleRequestDto request = ReCreateScheduleRequestDto.builder()
                .learningSourceId(learningSourceId.toString())
                .remainingDays(remainingDays)
                .dailyStudyTime(dailyStudyTime)
                .completedTaskTitles(completedTaskTitles)
                .remainingTaskTitles(remainingTaskTitles)
                .build();

        return webClient.post()
                .uri(fastApiBaseUrl + "/study/replan")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(FastApiChapterInfoDto.class)
                .collectList()
                .block();
    }

}

