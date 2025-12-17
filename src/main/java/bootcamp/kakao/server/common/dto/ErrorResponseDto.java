package bootcamp.kakao.server.common.dto;

import bootcamp.kakao.server.common.enums.Code;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ErrorResponseDto extends ResponseDto {

    private final Object data = null;

    public ErrorResponseDto(Code code) {
        super(code.getCode(), code.getMessage());
    }
}
