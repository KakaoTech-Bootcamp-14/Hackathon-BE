package bootcamp.kakao.server.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.processing.Pattern;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
    private String username;
    private String password;
    private String nickname;
}
