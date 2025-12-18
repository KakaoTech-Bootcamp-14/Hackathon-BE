package bootcamp.kakao.server.controller.swagger;

import bootcamp.kakao.server.common.dto.DataResponseDto;
import bootcamp.kakao.server.dto.schedule.CreateScheduleRequestDto;
import bootcamp.kakao.server.dto.schedule.CreateScheduleResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "스케줄 API", description = "학습 스케줄 생성 및 재생성 API")
public interface ScheduleControllerSpec {

    @Operation(
            summary = "학습 스케줄 생성",
            description = "PDF 파일과 학습 기간 정보를 입력받아 AI 기반 학습 스케줄(Chapter/Task)을 생성합니다."
    )
    DataResponseDto<CreateScheduleResponseDto> createSchedule(
            @Parameter(
                    description = "업로드할 PDF 파일",
                    content = @Content(mediaType = "multipart/form-data")
            )
            @RequestPart("multipartFile") MultipartFile multipartFile,

            @Parameter(
                    description = "학습 스케줄 생성 요청 정보",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateScheduleRequestDto.class))
            )
            @RequestPart("request") CreateScheduleRequestDto request
    );

    @Operation(
            summary = "학습 스케줄 재생성",
            description = "기존 학습 진행 상황을 기반으로 남은 학습 기간에 맞춰 스케줄을 재생성합니다."
    )
    DataResponseDto<CreateScheduleResponseDto> reCreateSchedule(
            @RequestBody CreateScheduleRequestDto request
    );
}
