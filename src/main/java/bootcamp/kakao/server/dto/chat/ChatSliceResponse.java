package bootcamp.kakao.server.dto.chat;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ChatSliceResponse {

    private final List<ChatMessageDto> chats;
    private final boolean hasNext;

    @Builder
    private ChatSliceResponse(List<ChatMessageDto> chats, boolean hasNext) {
        this.chats = chats;
        this.hasNext = hasNext;
    }
}
