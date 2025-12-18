package bootcamp.kakao.server.controller.swagger;

import bootcamp.kakao.server.common.dto.DataResponseDto;
import bootcamp.kakao.server.common.dto.ResponseDto;
import bootcamp.kakao.server.dto.learningsource.LearningSourceResponseDto;
import bootcamp.kakao.server.dto.learningsource.LearningSourceSummaryResponseDto;
import bootcamp.kakao.server.dto.learningsource.ProgressResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "학습 자료 API", description = "학습 자료 조회 및 요약 API")
public interface LearningSourceControllerSpec {

    @Operation(
            summary = "학습 자료 상세 조회",
            description = "학습 자료의 상세 정보와 챕터 목록을 조회합니다."
    )
    DataResponseDto<LearningSourceResponseDto> getLearningSourceChapter(
            @Parameter(description = "학습 자료 ID", example = "1")
            @PathVariable("learningSourceId") Long learningSourceId
    );

    @Operation(
            summary = "학습 자료 요약본 조회",
            description = "특정 Task에 대한 학습 자료 요약본을 AI 기반으로 생성하여 조회합니다."
    )
    DataResponseDto<LearningSourceSummaryResponseDto> getTaskLearningSourceSummary(
            @Parameter(description = "학습 자료 ID", example = "1")
            @PathVariable("learningSourceId") Long learningSourceId,

            @Parameter(description = "Task ID", example = "1")
            @PathVariable("taskId") Long taskId
    );

    @Operation(
            summary = "학습 자료 진도율 조회",
            description = "특정 학습 자료에 대한 진도율을 조회합니다. 전체 Task 개수, 완료된 Task 개수, 진도율(%)을 반환합니다."
    )
    DataResponseDto<ProgressResponseDto> getLearningSourceProgress(
            @Parameter(description = "학습 자료 ID", example = "1")
            @PathVariable("learningSourceId") Long learningSourceId
    );

    @Operation(
            summary = "학습 자료 삭제",
            description = "학습 자료와 관련된 chat, chapter, study plan, task 데이터를 함께 삭제합니다."
    )
    ResponseDto deleteLearningSource(
            @Parameter(description = "학습 자료 ID", example = "1")
            @PathVariable("learningSourceId") Long learningSourceId
    );

    @Operation(
            summary = "학습 자료의 모든 Task 완료 처리",
            description = "특정 학습 자료에 속한 모든 Task의 상태를 DONE으로 일괄 변경합니다."
    )
    ResponseDto completeAllTasks(
            @Parameter(description = "학습 자료 ID", example = "1")
            @PathVariable("learningSourceId") Long learningSourceId
    );
}
