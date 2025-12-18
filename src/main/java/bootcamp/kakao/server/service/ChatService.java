package bootcamp.kakao.server.service;

import bootcamp.kakao.server.client.FastApiClient;
import bootcamp.kakao.server.common.enums.Code;
import bootcamp.kakao.server.common.exception.GeneralException;
import bootcamp.kakao.server.domain.Chat;
import bootcamp.kakao.server.domain.LearningSource;
import bootcamp.kakao.server.dto.chat.*;
import bootcamp.kakao.server.repository.ChatRepository;
import bootcamp.kakao.server.repository.LearningSourceRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Service
public class ChatService {

    private final LearningSourceRepository learningSourceRepository;
    private final ChatRepository chatRepository;
    private final FastApiClient fastApiClient;

    public ChatService(
            LearningSourceRepository learningSourceRepository,
            ChatRepository chatRepository,
            FastApiClient fastApiClient
    ) {
        this.learningSourceRepository = learningSourceRepository;
        this.chatRepository = chatRepository;
        this.fastApiClient = fastApiClient;
    }

    @Transactional
    public ChatSendResponse sendMessage(Long learningSourceId, Long currentUserId, String userContent) {

        if (userContent == null || userContent.trim().isEmpty()) {
            throw new GeneralException(Code.INVALID_INPUT_VALUE);
        }

        LearningSource learningSource = learningSourceRepository.findById(learningSourceId)
                .orElseThrow(() -> new GeneralException(Code.NOT_FOUND));

        if (!learningSource.getUser().getId().equals(currentUserId)) {
            throw new GeneralException(Code.FORBIDDEN);
        }

        Chat userChat = chatRepository.save(Chat.createUserMessage(learningSource, userContent));

        FastApiChatResponse response = fastApiClient.chat(learningSourceId, userContent);
        String answerMd = response.getAnswerMd();
        if (answerMd == null || answerMd.trim().isEmpty()) {
            throw new GeneralException(Code.EXTERNAL_API_ERROR);
        }

        Chat assistantChat = chatRepository.save(Chat.createAssistantMessage(learningSource, answerMd));

        List<ChatSendResponse.Source> sources = (response.getSources() == null)
                ? Collections.emptyList()
                : response.getSources().stream()
                .map(s -> ChatSendResponse.Source.builder()
                        .source(s.getSource())
                        .page(s.getPage())
                        .build())
                .toList();

        return ChatSendResponse.builder()
                .userChatId(userChat.getId())
                .assistantChatId(assistantChat.getId())
                .assistantContent(answerMd)
                .sources(sources)
                .build();
    }

    @Transactional(readOnly = true)
    public ChatSliceResponse getChats(Long currentUserId, Long learningSourceId, Pageable pageable) {

        LearningSource learningSource = learningSourceRepository.findById(learningSourceId)
                .orElseThrow(() -> new GeneralException(Code.NOT_FOUND));

        if (!learningSource.getUser().getId().equals(currentUserId)) {
            throw new GeneralException(Code.FORBIDDEN);
        }

        Slice<Chat> slice = chatRepository.findByLearningSourceId(learningSourceId, pageable);

        List<ChatMessageDto> chats = slice.getContent().stream()
                .map(c -> ChatMessageDto.builder()
                        .chatId(c.getId())
                        .role(c.getRole())
                        .content(c.getContent())
                        .build())
                .toList();

        return ChatSliceResponse.builder()
                .chats(chats)
                .hasNext(slice.hasNext())
                .build();
    }
}