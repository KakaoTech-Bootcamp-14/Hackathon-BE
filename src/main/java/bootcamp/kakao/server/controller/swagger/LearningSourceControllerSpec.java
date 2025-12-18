package bootcamp.kakao.server.controller.swagger;

import bootcamp.kakao.server.common.dto.DataResponseDto;
import bootcamp.kakao.server.dto.learningsource.LearningSourceResponseDto;
import bootcamp.kakao.server.dto.learningsource.LearningSourceSummaryResponseDto;
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
}
