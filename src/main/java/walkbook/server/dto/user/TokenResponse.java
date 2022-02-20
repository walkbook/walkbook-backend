package walkbook.server.dto.user;

import lombok.Getter;
import lombok.Setter;
import walkbook.server.dto.CommonResponse;

@Getter
@Setter
public class TokenResponse<T> extends CommonResponse {
    private String token;
    private T data;
}
