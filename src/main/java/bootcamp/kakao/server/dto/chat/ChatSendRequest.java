package bootcamp.kakao.server.dto.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatSendRequest {

    @Schema(description = "채팅 메시지 내용", example = "Chapter 1의 핵심 개념을 설명해주세요.")
    private String content;
}
