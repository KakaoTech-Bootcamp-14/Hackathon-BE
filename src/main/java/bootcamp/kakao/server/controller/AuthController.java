package bootcamp.kakao.server.controller;

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
    public ResponseEntity<ApiResponse<Void>> signUp(@RequestBody SignUpRequest signUpRequest) {
        authService.signUp(signUpRequest);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}



