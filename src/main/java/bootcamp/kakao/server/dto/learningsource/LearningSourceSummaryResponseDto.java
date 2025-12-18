package bootcamp.kakao.server.dto.learningsource;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LearningSourceSummaryResponseDto {
    @JsonProperty("study_session_id")
    private String learningSourceId;
    @JsonProperty("topic")
    private String taskTitle;
    @JsonProperty("content_md")
    private String content;
}
