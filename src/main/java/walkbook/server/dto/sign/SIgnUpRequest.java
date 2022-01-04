package walkbook.server.dto.sign;

import lombok.*;
import walkbook.server.enums.Gender;

@Getter
@Setter
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
}
