package bootcamp.kakao.server.dto.chat;

import bootcamp.kakao.server.domain.ChatRole;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatMessageDto {

    private final Long chatId;
    private final ChatRole role;
    private final String content;

    @Builder
    private ChatMessageDto(Long chatId, ChatRole role, String content) {
        this.chatId = chatId;
        this.role = role;
        this.content = content;
    }
}
