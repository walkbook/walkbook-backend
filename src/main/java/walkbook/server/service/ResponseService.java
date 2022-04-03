package walkbook.server.service;

import walkbook.server.dto.CommonResponse;
import walkbook.server.dto.ListResponse;
import walkbook.server.dto.SingleResponse;
import walkbook.server.dto.user.TokenResponse;

import java.util.List;

public interface ResponseService {
    <T> SingleResponse<T> getSingleResult(T data);
    <T> TokenResponse<T> getTokenResult(T data, String token);
    <T> ListResponse<T> getListResult(List<T> list);
    CommonResponse getSuccessResult();
    CommonResponse getFailResult(int code, String msg);
}
