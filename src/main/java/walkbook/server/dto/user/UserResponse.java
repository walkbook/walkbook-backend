package walkbook.server.dto.user;

import lombok.*;
import walkbook.server.enums.Gender;

@Getter
@Builder
public class UserResponse {
    private final Long userId;
    private final String username;
    private final String nickname;
    private final Gender gender;
    private final String age;
    private final String location;
    private final String introduction;
}
