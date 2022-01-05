package walkbook.server.dto.sign;

import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import walkbook.server.domain.Gender;
import walkbook.server.domain.User;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SIgnUpRequest {
    private String username;
    private String password;
    private String nickname;
    private Gender gender;
    private String age;
    private String location;
    private String introduction;

    public User toEntity(PasswordEncoder passwordEncoder) {
        return User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .gender(gender)
                .age(age)
                .location(location)
                .introduction(introduction)
                .build();
    }
}
