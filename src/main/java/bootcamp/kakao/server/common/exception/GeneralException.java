package bootcamp.kakao.server.common.exception;


import bootcamp.kakao.server.common.enums.Code;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException{

    private final Code code;

    public GeneralException(Code code) {
        super(code.getMessage());
        this.code = code;
    }

}
