package bootcamp.kakao.server.client;

import bootcamp.kakao.server.dto.chat.FastApiChatRequest;
import bootcamp.kakao.server.dto.chat.FastApiChatResponse;
import bootcamp.kakao.server.dto.learningsource.LearningSourceSummaryRequestDto;
import bootcamp.kakao.server.dto.learningsource.LearningSourceSummaryResponseDto;
import bootcamp.kakao.server.dto.schedule.FastApiChapterInfoDto;
import bootcamp.kakao.server.dto.schedule.ReCreateScheduleRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Slf4j
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
        log.info("[FastAPI Request] POST /documents/upload - learningSourceId: {}, fileName: {}",
                learningSourceId, multipartFile.getOriginalFilename());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        Resource resource = multipartFile.getResource();
        body.add("file", resource);
        body.add("study_session_id", learningSourceId);

        try {
            webClient.post()
                    .uri(fastApiBaseUrl + "/documents/upload")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(body))
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.info("[FastAPI Response] POST /documents/upload - Success");
        } catch (WebClientResponseException e) {
            log.error("[FastAPI Error] POST /documents/upload - Status: {}, Body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    /**
     * PDF + 학습 기간 기반 Chapter/Task 생성 요청
     */
    public List<FastApiChapterInfoDto> createChapters(
            int totalDays,
            int dailyStudyTime,
            Long learningSourceId
    ) {
        log.info("[FastAPI Request] POST /study/plan - learningSourceId: {}, totalDays: {}, dailyStudyTime: {}",
                learningSourceId, totalDays, dailyStudyTime);

        Map<String, Object> body = Map.of(
                "total_days", totalDays,
                "hours_per_day", dailyStudyTime,
                "study_session_id", learningSourceId.toString()
        );

        try {
            List<FastApiChapterInfoDto> response = webClient.post()
                    .uri(fastApiBaseUrl + "/study/plan")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToFlux(FastApiChapterInfoDto.class)
                    .collectList()
                    .block();

            log.info("[FastAPI Response] POST /study/plan - Chapters count: {}", response != null ? response.size() : 0);
            log.debug("[FastAPI Response] POST /study/plan - Response: {}", response);

            return response;
        } catch (WebClientResponseException e) {
            log.error("[FastAPI Error] POST /study/plan - Status: {}, Body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    public List<FastApiChapterInfoDto> rescheduleChapters(
            Long learningSourceId,
            int remainingDays,
            int dailyStudyTime,
            List<String> completedTaskTitles,
            List<String> remainingTaskTitles
    ) {
        log.info("[FastAPI Request] POST /study/replan - learningSourceId: {}, remainingDays: {}, dailyStudyTime: {}, completed: {}, remaining: {}",
                learningSourceId, remainingDays, dailyStudyTime, completedTaskTitles.size(), remainingTaskTitles.size());

        ReCreateScheduleRequestDto request = ReCreateScheduleRequestDto.builder()
                .learningSourceId(learningSourceId.toString())
                .remainingDays(remainingDays)
                .dailyStudyTime(dailyStudyTime)
                .completedTaskTitles(completedTaskTitles)
                .remainingTaskTitles(remainingTaskTitles)
                .build();

        try {
            List<FastApiChapterInfoDto> response = webClient.post()
                    .uri(fastApiBaseUrl + "/study/replan")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToFlux(FastApiChapterInfoDto.class)
                    .collectList()
                    .block();

            log.info("[FastAPI Response] POST /study/replan - Chapters count: {}", response != null ? response.size() : 0);
            log.debug("[FastAPI Response] POST /study/replan - Response: {}", response);

            return response;
        } catch (WebClientResponseException e) {
            log.error("[FastAPI Error] POST /study/replan - Status: {}, Body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    public LearningSourceSummaryResponseDto getLearningSourceSummary(Long learningSourceId, String taskTitle) {
        log.info("[FastAPI Request] POST /study/material - learningSourceId: {}, taskTitle: {}",
                learningSourceId, taskTitle);

        LearningSourceSummaryRequestDto request = LearningSourceSummaryRequestDto.builder()
                .learningSourceId(learningSourceId.toString())
                .taskTitle(taskTitle)
                .build();

        try {
            LearningSourceSummaryResponseDto response = webClient.post()
                    .uri(fastApiBaseUrl + "/study/material")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(LearningSourceSummaryResponseDto.class)
                    .block();

            log.info("[FastAPI Response] POST /study/material - Success");
            log.debug("[FastAPI Response] POST /study/material - Response: {}", response);

            return response;
        } catch (WebClientResponseException e) {
            log.error("[FastAPI Error] POST /study/material - Status: {}, Body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    public FastApiChatResponse chat(Long learningSourceId, String question) {
        log.info("[FastAPI Request] POST /chat - learningSourceId: {}, question: {}",
                learningSourceId, question);

        Map<String, Object> body = Map.of(
                "study_session_id", learningSourceId.toString(),
                "question", question
        );

        try {
            FastApiChatResponse response = webClient.post()
                    .uri(fastApiBaseUrl + "/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(FastApiChatResponse.class)
                    .block();

            log.info("[FastAPI Response] POST /chat - Success");
            log.debug("[FastAPI Response] POST /chat - Response: {}", response);

            return response;
        } catch (WebClientResponseException e) {
            log.error("[FastAPI Error] POST /chat - Status: {}, Body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

}

