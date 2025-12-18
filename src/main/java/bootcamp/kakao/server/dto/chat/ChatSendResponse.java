package bootcamp.kakao.server.dto.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
public class ChatSendResponse {
    private final Long userChatId;
    private final Long assistantChatId;
    private final String assistantContent;
    private final List<Source> sources;

    @Builder
    private ChatSendResponse(Long userChatId, Long assistantChatId, String assistantContent, List<Source> sources) {
        this.userChatId = userChatId;
        this.assistantChatId = assistantChatId;
        this.assistantContent = assistantContent;
        this.sources = sources;
    }

    @Getter
    public static class Source {
        private String source;
        private Integer page;

        @Builder
        private Source(String source, Integer page) {
            this.source = source;
            this.page = page;
        }
    }
}
