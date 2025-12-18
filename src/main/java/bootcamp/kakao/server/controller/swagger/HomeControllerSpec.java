package bootcamp.kakao.server.controller.swagger;

import bootcamp.kakao.server.common.dto.DataResponseDto;
import bootcamp.kakao.server.dto.home.HomeResponseDto;
import bootcamp.kakao.server.dto.learningsource.ProgressResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "홈 캘린더 API", description = "캘린더 홈 화면의 할 일 목록 조회 API")
public interface HomeControllerSpec {

    @Operation(
            summary = "캘린더 할 일 목록 조회",
            description = "캘린더 홈 화면에 보여줄 학습 자료 및 할 일 목록을 조회합니다."
    )
    DataResponseDto<HomeResponseDto> getHomeData(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable("userId") Long userId
    );

    @Operation(
            summary = "사용자 전체 진도율 조회",
            description = "사용자의 모든 학습 자료에 대한 전체 진도율을 조회합니다. 전체 Task 개수, 완료된 Task 개수, 진도율(%)을 반환합니다."
    )
    DataResponseDto<ProgressResponseDto> getOverallProgress(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable("userId") Long userId
    );
}
