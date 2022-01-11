package walkbook.server.service.response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import walkbook.server.dto.CommonResponse;
import walkbook.server.dto.ListResponse;
import walkbook.server.dto.SingleResponse;
import walkbook.server.dto.sign.TokenResponse;

import java.util.List;

@Service
@Slf4j
public class ResponseService {
    public <T> SingleResponse<T> getSingleResult(T data) {
        SingleResponse<T> result = new SingleResponse<>();
        result.setData(data);
        setSuccessResult(result);
        return result;
    }

    public <T> TokenResponse<T> getTokenResult(T data, String token) {
        TokenResponse<T> result = new TokenResponse<>();
        result.setData(data);
        result.setToken(token);
        setSuccessResult(result);
        return result;
    }

    public <T> ListResponse<T> getListResult(List<T> list) {
        ListResponse<T> result = new ListResponse<>();
        result.setList(list);
        setSuccessResult(result);
        return result;
    }

    public CommonResponse getSuccessResult() {
        CommonResponse result = new CommonResponse();
        setSuccessResult(result);
        return result;
    }

    public CommonResponse getFailResult(int code, String msg) {
        CommonResponse result = new CommonResponse();
        result.setSuccess(false);
        setFailResult(result, code, msg);
        return result;
    }

    private void setSuccessResult(CommonResponse result) {
        result.setSuccess(true);
        result.setCode(walkbook.server.service.response.CommonResponse.SUCCESS.getCode());
        result.setMsg(walkbook.server.service.response.CommonResponse.SUCCESS.getMsg());
    }

    private void setFailResult(CommonResponse result, int code, String msg) {
        result.setSuccess(false);
        result.setCode(code);
        result.setMsg(msg);
    }
}
