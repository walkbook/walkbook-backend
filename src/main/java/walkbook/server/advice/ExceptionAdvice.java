package walkbook.server.advice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import walkbook.server.advice.exception.*;
import walkbook.server.dto.CommonResponse;
import walkbook.server.service.response.ResponseService;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {
    private final ResponseService responseService;
    private final MessageSource messageSource;

    /***
     * -9999
     * default Exception
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResponse defaultException(HttpServletRequest request, Exception e) {
        return responseService.getFailResult
                (Integer.parseInt(getMessage("unKnown.code")), getMessage("unKnown.msg"));
    }

    /***
     * -1000
     * 유저를 찾지 못했을 때 발생시키는 예외
     */
    @ExceptionHandler(CUserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResponse userNotFoundException(HttpServletRequest request, CUserNotFoundException e) {
        return responseService.getFailResult(
                Integer.parseInt(getMessage("userNotFound.code")), getMessage("userNotFound.msg")
        );
    }

    /***
     * -1001
     * 유저 로그인 실패 시 발생시키는 예외
     */
    @ExceptionHandler(CLoginFailedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    protected CommonResponse loginFailedException(HttpServletRequest request, CLoginFailedException e) {
        return responseService.getFailResult(
                Integer.parseInt(getMessage("loginFailed.code")), getMessage("loginFailed.msg")
        );
    }

    /***
     * -1002
     * 회원 가입 시 이미 가입된 아이디인 경우 발생 시키는 예외
     */
    @ExceptionHandler(CSignupFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResponse signupFailedException(HttpServletRequest request, CSignupFailedException e) {
        return responseService.getFailResult(
                Integer.parseInt(getMessage("signupFailed.code")), getMessage("signupFailed.msg")
        );
    }

    /**
     * -1003
     * 액세스 토큰 만료시 발생하는 에러
     */
    @ExceptionHandler(CExpiredAccessTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected CommonResponse expiredAccessTokenException(HttpServletRequest request, CExpiredAccessTokenException e) {
        return responseService.getFailResult(
                Integer.parseInt(getMessage("expiredAccessToken.code")), getMessage("expiredAccessToken.msg")
        );
    }
    /**
     * -1004
     * 액세스 토큰 에러시 발생하는 에러
     */
    @ExceptionHandler(CAccessTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected CommonResponse accessTokenException(HttpServletRequest request, CExpiredAccessTokenException e) {
        return responseService.getFailResult(
                Integer.parseInt(getMessage("accessToken.code")), getMessage("accessToken.msg")
        );
    }

    /**
    * -2000
    * 존재하지 않는 포스트일 때 발생하는 에러
    */
    @ExceptionHandler(CPostNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResponse postNotFoundException(HttpServletRequest request, CPostNotFoundException e) {
        return responseService.getFailResult(
                Integer.parseInt(getMessage("postNotFoundException.code")), getMessage("postNotFoundException.msg")
        );
    }

    private String getMessage(String code) {
        return getMessage(code, null);
    }

    private String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
