package bootcamp.kakao.server.controller;

import bootcamp.kakao.server.common.dto.DataResponseDto;
import bootcamp.kakao.server.common.dto.ResponseDto;
import bootcamp.kakao.server.common.enums.Code;
import bootcamp.kakao.server.dto.auth.LoginRequest;
import bootcamp.kakao.server.dto.auth.LoginResponse;
import bootcamp.kakao.server.dto.auth.SignUpRequest;
import bootcamp.kakao.server.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseDto> signUp(@RequestBody SignUpRequest signUpRequest) {
        authService.signUp(signUpRequest);
        return ResponseEntity.ok(new ResponseDto(Code.OK.getCode(), Code.OK.getMessage()));
    }

    @PostMapping("/login")
    public ResponseEntity<DataResponseDto<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(new DataResponseDto<>(Code.OK, response));
    }
}



