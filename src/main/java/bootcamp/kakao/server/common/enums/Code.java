package bootcamp.kakao.server.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum Code {

    /*

    1. Sxxx : 성공 응답
	2. EXxxx : 실패 응답

     */

    // 성공 응답 (Sxxx)
    OK(HttpStatus.OK.value(),"S001", "요청이 성공하였습니다."),


    // 클라이언트 오류 (ECxxx)
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "EC001", "잘못된 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND.value(), "EC002", "요청한 리소스를 찾을 수 없습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST.value(), "EC003", "유효하지 않은 값을 입력하였습니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST.value(), "EC004", "파일 크기가 허용된 한도를 초과했습니다."),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST.value(), "EC005", "허용되지 않은 파일 형식입니다."),
    FILE_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "EC006", "업로드된 파일을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "EC007", "댓글을 찾을 수 없습니다."),
    POST_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "EC008", "게시글 이미지를 찾을 수 없습니다."),
    INVALID_INPUT_FORMAT(HttpStatus.BAD_REQUEST.value(), "EC009", "유효하지 않은 형식의 요청입니다."),
    USER_MISMATCH(HttpStatus.UNAUTHORIZED.value(), "EC010", "이메일 또는 비밀번호가 일치하지 않습니다"),
    PASSWORD_CONFLICT(HttpStatus.CONFLICT.value(), "EC011", "이전 비밀번호와 동일한 비밀번호입니다."),
    NICKNAME_CONFLICT(HttpStatus.CONFLICT.value(), "EC012", "이미 존재하는 닉네임입니다."),
    EMAIL_CONFLICT(HttpStatus.CONFLICT.value(), "EC013", "이미 존재하는 이메일입니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST.value(), "EC014", "올바르지 않은 이메일 형식입니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST.value(), "EC015", "올바르지 않은 비밀번호 형식입니다."),
    IDENTITY_DATA(HttpStatus.CONFLICT.value(), "EC016", "변경 사항이 존재하지 않습니다."),


    // 서버 오류 (ESxxx),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ES001", "예기치 못한 서버 에러가 발생했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ES002", "데이터베이스 처리 중 오류가 발생했습니다."),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ES003", "파일 업로드 중 오류가 발생했습니다."),
    EXTERNAL_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ES004", "외부 API 호출 중 오류가 발생했습니다."),


    // 인증,인가 오류 (EUxxx)
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "EU001", "인증되지 않은 사용자입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED.value(), "EU002", "유효하지 않은 토큰입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN.value(), "EU003", "접근이 허용되지 않습니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "EU004", "존재하지 않는 회원입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED.value(), "EU005", "토큰이 만료되었습니다."),

    // 비즈니스 오류 (EBxxx)
    POST_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "EB001", "해당 게시글이 존재하지 않습니다."),
    POST_MISMATCH(HttpStatus.NOT_FOUND.value(), "EB002", "해당 게시글의 타입이 올바르지 않습니다."),
    POST_EDIT_FORBIDDEN(HttpStatus.FORBIDDEN.value(), "EB003", "해당 게시글의 수정 권한이 없습니다."),
    POST_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN.value(), "EB003", "해당 게시글의 삭제 권한이 없습니다."),
    COMMENT_EDIT_FORBIDDEN(HttpStatus.FORBIDDEN.value(), "EB005", "해당 댓글의 수정 권한이 없습니다."),
    COMMENT_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN.value(), "EB005", "해당 댓글의 삭제 권한이 없습니다."),
    PASSWORD_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "EB006", "비밀번호가 존재하지 않습니다."),
    POST_DELETED(HttpStatus.NOT_FOUND.value(), "EB007", "해당 게시글이 삭제되었습니다."),
    COMMENT_DELETED(HttpStatus.NOT_FOUND.value(), "EB008", "해당 댓글이 삭제되었습니다.");

    private final int status;
    private final String code;
    private final String message;

}