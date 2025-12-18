package bootcamp.kakao.server.dto.schedule;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class CreateScheduleResponseDto {

    private List<ChapterInfoDto> chapterInfoDtos;

}
