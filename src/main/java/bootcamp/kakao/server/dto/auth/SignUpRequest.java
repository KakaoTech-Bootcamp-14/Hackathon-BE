package bootcamp.kakao.server.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.processing.Pattern;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    @Schema(description = "사용자 아이디", example = "zeus_user")
    private String username;

    @Schema(description = "비밀번호", example = "password123!")
    private String password;

    @Schema(description = "닉네임", example = "제우스")
    private String nickname;
}
