package bootcamp.kakao.server.controller.swagger;

import bootcamp.kakao.server.common.dto.DataResponseDto;
import bootcamp.kakao.server.common.dto.ResponseDto;
import bootcamp.kakao.server.dto.auth.LoginRequest;
import bootcamp.kakao.server.dto.auth.LoginResponse;
import bootcamp.kakao.server.dto.auth.SignUpRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "인증 API", description = "회원가입 및 로그인 API")
public interface AuthControllerSpec {

    @Operation(
            summary = "회원가입",
            description = "사용자 아이디, 비밀번호, 닉네임을 입력받아 회원가입을 진행합니다."
    )
    ResponseEntity<ResponseDto> signUp(@RequestBody SignUpRequest signUpRequest);

    @Operation(
            summary = "로그인",
            description = "사용자 아이디와 비밀번호로 로그인하여 사용자 정보와 토큰을 반환합니다."
    )
    ResponseEntity<DataResponseDto<LoginResponse>> login(@RequestBody LoginRequest loginRequest);
}
