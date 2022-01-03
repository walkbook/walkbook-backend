package walkbook.server.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import walkbook.server.enums.Gender;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SIgnUpRequest {
    @NotNull
    @Size(min = 3, max = 20)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    @Size(min = 3, max = 100)
    private String password;

    @Size(min = 3, max = 20)
    private String nickname;

    private Gender gender;

    private String age;

    private String location;

    private String introduction;
}
