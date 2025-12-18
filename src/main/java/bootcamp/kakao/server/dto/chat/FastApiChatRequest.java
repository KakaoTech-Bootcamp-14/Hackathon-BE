package bootcamp.kakao.server.dto.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class FastApiChatRequest {

    @JsonProperty("study_session_id")
    private final Long studySessionId;

    @JsonProperty("question")
    private final String question;

    @Builder
    private FastApiChatRequest(Long learningSourceId, String question) {
        this.studySessionId = learningSourceId;
        this.question = question;
    }
}