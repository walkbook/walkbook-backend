package walkbook.server.dto.user;

import lombok.*;
import walkbook.server.domain.Gender;
import walkbook.server.domain.User;

@Getter
public class UserResponse {
    private final Long userId;
    private final String username;
    private final String nickname;
    private final Gender gender;
    private final int age;
    private final String location;
    private final String introduction;

    public UserResponse(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.gender = user.getGender();
        this.age = user.getAge();
        this.location = user.getLocation();
        this.introduction = user.getIntroduction();
    }
}