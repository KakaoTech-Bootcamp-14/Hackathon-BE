package bootcamp.kakao.server.service;

import bootcamp.kakao.server.common.enums.Code;
import bootcamp.kakao.server.common.exception.GeneralException;
import bootcamp.kakao.server.domain.User;
import bootcamp.kakao.server.dto.auth.LoginRequest;
import bootcamp.kakao.server.dto.auth.LoginResponse;
import bootcamp.kakao.server.dto.auth.SignUpRequest;
import bootcamp.kakao.server.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void signUp(SignUpRequest request) {
        User user = User.createUser(request.getUsername(), request.getPassword(), request.getNickname());
        userRepository.save(user);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new GeneralException(Code.USER_NOT_FOUND));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "password not match");
        }

        return new LoginResponse(user.getId(), user.getNickname());
    }
}
