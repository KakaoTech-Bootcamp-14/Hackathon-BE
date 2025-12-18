package bootcamp.kakao.server.dto.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class CreateScheduleRequestDto {

    @Schema(description = "학습 자료 제목", example = "Spring Boot 완벽 가이드")
    private String learningSourceTitle;

    @Schema(description = "학습 시작 날짜", example = "2025-01-01")
    private LocalDate startDate;

    @Schema(description = "학습 종료 날짜", example = "2025-01-31")
    private LocalDate endDate;

    @Schema(description = "주말 제외 여부", example = "true")
    private boolean excludeWeekend;

    @Schema(description = "일일 학습 시간(시간)", example = "2")
    private int dailyStudyTime;
}
