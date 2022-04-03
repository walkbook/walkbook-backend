package walkbook.server.service;

import org.springframework.stereotype.Service;
import walkbook.server.dto.CommonResponse;
import walkbook.server.dto.ListResponse;
import walkbook.server.dto.SingleResponse;
import walkbook.server.dto.user.TokenResponse;

import java.util.List;

@Service
public class ResponseServiceImpl implements ResponseService {
    @Override
    public <T> SingleResponse<T> getSingleResult(T data) {
        SingleResponse<T> result = new SingleResponse<>();
        result.setData(data);
        setSuccessResult(result);
        return result;
    }

    @Override
    public <T> TokenResponse<T> getTokenResult(T data, String token) {
        TokenResponse<T> result = new TokenResponse<>();
        result.setData(data);
        result.setToken(token);
        setSuccessResult(result);
        return result;
    }

    @Override
    public <T> ListResponse<T> getListResult(List<T> list) {
        ListResponse<T> result = new ListResponse<>();
        result.setData(list);
        setSuccessResult(result);
        return result;
    }

    @Override
    public CommonResponse getSuccessResult() {
        CommonResponse result = new CommonResponse();
        setSuccessResult(result);
        return result;
    }

    @Override
    public CommonResponse getFailResult(int code, String msg) {
        CommonResponse result = new CommonResponse();
        result.setSuccess(false);
        setFailResult(result, code, msg);
        return result;
    }

    private void setSuccessResult(CommonResponse result) {
        result.setSuccess(true);
        result.setCode(ResponseEnum.SUCCESS.getCode());
        result.setMsg(ResponseEnum.SUCCESS.getMsg());
    }

    private void setFailResult(CommonResponse result, int code, String msg) {
        result.setSuccess(false);
        result.setCode(code);
        result.setMsg(msg);
    }
}
