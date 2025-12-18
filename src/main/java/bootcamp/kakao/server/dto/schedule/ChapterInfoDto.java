package bootcamp.kakao.server.dto.schedule;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ChapterInfoDto {
    private Long chapterId;
    private int chapterOrder;
    private String chapterTitle;
    List<TaskInfoDto> taskInfoDtos;
}
