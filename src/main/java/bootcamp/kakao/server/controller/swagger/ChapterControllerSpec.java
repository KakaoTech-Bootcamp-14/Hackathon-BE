package bootcamp.kakao.server.controller.swagger;

import bootcamp.kakao.server.common.dto.DataResponseDto;
import bootcamp.kakao.server.common.dto.ResponseDto;
import bootcamp.kakao.server.dto.learningsource.ProgressResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "챕터 API", description = "챕터 진도율 조회 및 완료 처리 API")
public interface ChapterControllerSpec {

    @Operation(
            summary = "챕터 진도율 조회",
            description = "특정 챕터에 대한 진도율을 조회합니다. 전체 Task 개수, 완료된 Task 개수, 진도율(%)을 반환합니다."
    )
    DataResponseDto<ProgressResponseDto> getChapterProgress(
            @Parameter(description = "챕터 ID", example = "1")
            @PathVariable("chapterId") Long chapterId
    );

    @Operation(
            summary = "챕터의 모든 Task 완료 처리",
            description = "특정 챕터에 속한 모든 Task의 상태를 DONE으로 일괄 변경합니다."
    )
    ResponseDto completeAllTasksByChapter(
            @Parameter(description = "챕터 ID", example = "1")
            @PathVariable("chapterId") Long chapterId
    );
}
