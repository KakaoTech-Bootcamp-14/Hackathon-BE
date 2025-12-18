package bootcamp.kakao.server.dto.learningsource;

import bootcamp.kakao.server.dto.schedule.ChapterInfoDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LearningSourceResponseDto {

    private Long learningSourceId;
    private String learningSourceTitle;
    private List<ChapterInfoDto> chapterInfoDtos;

}
