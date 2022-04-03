package walkbook.server.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseEnum {
    SUCCESS(0, "성공"),
    FAIL(-1, "실패");

    private int code;
    private String msg;
}
