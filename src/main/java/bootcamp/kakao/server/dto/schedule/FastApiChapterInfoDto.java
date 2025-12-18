package bootcamp.kakao.server.dto.schedule;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FastApiChapterInfoDto {

    private Integer chapterOrder;
    private String chapterTitle;
    private List<FastApiTaskInfoDto> tasks;
}
