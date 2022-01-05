package walkbook.server.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import walkbook.server.domain.Gender;
import walkbook.server.domain.User;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    private String username;
    private String nickname;
    private Gender gender;
    private String age;
    private String location;
    private String introduction;

    public User toEntity() {
        return User.builder()
                .username(username)
                .nickname(nickname)
                .gender(gender)
                .age(age)
                .location(location)
                .introduction(introduction)
                .build();
    }
}