package bootcamp.kakao.server.dto.learningsource;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProgressResponseDto {
    private long totalTaskCount;
    private long doneTaskCount;
    private int progressRate;
}
