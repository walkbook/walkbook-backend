package walkbook.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonResponse {
    private boolean success;
    private int code;
    private String msg;
}
