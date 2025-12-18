package bootcamp.kakao.server.dto.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class FastApiChatResponse {

    @JsonProperty("study_session_id")
    private String studySessionId;

    private String question;

    @JsonProperty("answer_md")
    private String answerMd;

    private List<SourceRef> sources;

    @Getter
    @NoArgsConstructor
    public static class SourceRef {
        private String source;
        private Integer page;
    }
}