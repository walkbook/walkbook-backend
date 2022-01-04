package walkbook.server.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class TokenResponse {
    private final String token;

    public TokenResponse(String token){
        this.token = token;
    }
}
