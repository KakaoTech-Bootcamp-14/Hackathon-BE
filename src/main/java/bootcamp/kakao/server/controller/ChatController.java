package bootcamp.kakao.server.controller;

import bootcamp.kakao.server.common.dto.DataResponseDto;
import bootcamp.kakao.server.common.enums.Code;
import bootcamp.kakao.server.dto.chat.ChatSendRequest;
import bootcamp.kakao.server.dto.chat.ChatSendResponse;
import bootcamp.kakao.server.dto.chat.ChatSliceResponse;
import bootcamp.kakao.server.service.ChatService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning-sources")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/{learningSourceId}/chat")
    public ResponseEntity<DataResponseDto<ChatSliceResponse>> getChats(
            @PathVariable Long learningSourceId,
            @RequestParam Long currentUserId,
            @PageableDefault(size = 50, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        ChatSliceResponse chats = chatService.getChats(currentUserId, learningSourceId, pageable);
        return ResponseEntity.ok(new DataResponseDto<>(Code.OK, chats));
    }

    @PostMapping("/{learningSourceId}/chat")
    public ResponseEntity<DataResponseDto<ChatSendResponse>> send(
            @PathVariable Long learningSourceId,
            @RequestParam Long currentUserId,
            @RequestBody ChatSendRequest request
    ) {
        ChatSendResponse response = chatService.sendMessage(learningSourceId, currentUserId, request.getContent());
        return ResponseEntity.ok(new DataResponseDto<>(Code.OK, response));
    }

}