package bootcamp.kakao.server.dto.schedule;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class CreateScheduleResponseDto {
    private Long learningSourceId;
    private String learningSourceTitle;
    private List<ChapterInfoDto> chapterInfoDtos;

}
