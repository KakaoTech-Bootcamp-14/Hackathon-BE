package bootcamp.kakao.server.controller.swagger;

import bootcamp.kakao.server.common.dto.DataResponseDto;
import bootcamp.kakao.server.dto.chat.ChatSendRequest;
import bootcamp.kakao.server.dto.chat.ChatSendResponse;
import bootcamp.kakao.server.dto.chat.ChatSliceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "채팅 API", description = "학습 자료 기반 AI 챗봇과의 대화 API")
public interface ChatControllerSpec {

    @Operation(
            summary = "채팅 목록 조회",
            description = "특정 학습 자료에 대한 채팅 히스토리를 페이지네이션으로 조회합니다."
    )
    ResponseEntity<DataResponseDto<ChatSliceResponse>> getChats(
            @Parameter(description = "학습 자료 ID", example = "1")
            @PathVariable Long learningSourceId,

            @Parameter(description = "현재 사용자 ID", example = "1")
            @RequestParam Long currentUserId,

            @Parameter(description = "페이지네이션 정보 (size=50, sort=id,desc)")
            Pageable pageable
    );

    @Operation(
            summary = "채팅 메시지 전송",
            description = "학습 자료 기반 AI 챗봇에게 메시지를 전송하고 응답을 받습니다."
    )
    ResponseEntity<DataResponseDto<ChatSendResponse>> send(
            @Parameter(description = "학습 자료 ID", example = "1")
            @PathVariable Long learningSourceId,

            @Parameter(description = "현재 사용자 ID", example = "1")
            @RequestParam Long currentUserId,

            @RequestBody ChatSendRequest request
    );
}
