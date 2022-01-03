package walkbook.server.payload;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private Boolean success;
    private String message;
}
