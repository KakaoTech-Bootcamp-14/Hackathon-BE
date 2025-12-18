package bootcamp.kakao.server.controller;

import bootcamp.kakao.server.common.dto.DataResponseDto;
import bootcamp.kakao.server.common.dto.ResponseDto;
import bootcamp.kakao.server.common.enums.Code;
import bootcamp.kakao.server.controller.swagger.LearningSourceControllerSpec;
import bootcamp.kakao.server.dto.learningsource.LearningSourceResponseDto;
import bootcamp.kakao.server.dto.learningsource.LearningSourceSummaryResponseDto;
import bootcamp.kakao.server.service.LearningSourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/learning-source")
@RequiredArgsConstructor
public class LearningSourceController implements LearningSourceControllerSpec {

    private final LearningSourceService learningSourceService;

    @GetMapping("/{learningSourceId}")
    public DataResponseDto<LearningSourceResponseDto> getLearningSourceChapter(@PathVariable ("learningSourceId") Long learningSourceId) {
        LearningSourceResponseDto learningSourceResponseDto = learningSourceService.getLearningSourceDetails(learningSourceId);
        return new DataResponseDto<>(Code.OK, "학습 자료 상세데이터를 성공적으로 조회하였습니다.", learningSourceResponseDto);
    }

    @PostMapping("/{learningSourceId}/{taskId}/summary")
    public DataResponseDto<LearningSourceSummaryResponseDto> getTaskLearningSourceSummary(@PathVariable ("learningSourceId") Long learningSourceId,
                                                                                          @PathVariable ("taskId") Long taskId) {
        LearningSourceSummaryResponseDto taskLearningSourceSummaryResponseDto = learningSourceService.getLearningSourceSummary(learningSourceId, taskId);
        return new DataResponseDto<>(Code.OK, "학습 자료 요약본을 성공적으로 조회하였습니다.", taskLearningSourceSummaryResponseDto);
    }

    @DeleteMapping("/{learningSourceId}")
    public ResponseDto deleteLearningSource(@PathVariable("learningSourceId") Long learningSourceId) {
        learningSourceService.deleteLearningSource(learningSourceId);
        return new ResponseDto(Code.OK.getCode(), "학습 자료가 성공적으로 삭제되었습니다.");
    }
}
