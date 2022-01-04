package walkbook.server.payload;

import lombok.*;

@Getter
@Setter
@Builder
public class TokenResponse {
    private final String tokenType = "Bearer";
    private final String token;

    public TokenResponse(String token){
        this.token = token;
    }
}
