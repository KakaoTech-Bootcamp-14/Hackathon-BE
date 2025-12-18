package bootcamp.kakao.server.dto.home;

import bootcamp.kakao.server.dto.learningsource.LearningSourceResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HomeResponseDto {

    private Long userId;
    private String nickname;
    private List<LearningSourceResponseDto> learningSourceResponseDtos;
}
