package walkbook.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import walkbook.server.advice.exception.CLoginFailedException;
import walkbook.server.domain.User;
import walkbook.server.dto.sign.TokenResponse;
import walkbook.server.dto.user.UserResponse;
import walkbook.server.dto.sign.SignInRequest;
import walkbook.server.dto.sign.SIgnUpRequest;
import walkbook.server.dto.CommonResponse;
import walkbook.server.dto.SingleResponse;
import walkbook.server.repository.UserRepository;
import walkbook.server.service.SignService;
import walkbook.server.service.UserService;
import walkbook.server.service.response.ResponseService;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/api/user")
public class SignController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ResponseService responseService;
    @Autowired
    SignService signService;
    @Autowired
    UserService userService;

    @PostMapping("/signin")
    public TokenResponse createAuthenticationToken(@Valid @RequestBody SignInRequest signInRequest) {
        String username = signInRequest.getUsername();
        final String token = signService.signin(signInRequest);
        return responseService.getTokenResult(userService.findByUsername(username), token);
    }

    @PostMapping("/signup")
    public CommonResponse registerUser(@Valid @RequestBody SIgnUpRequest sIgnUpRequest) {
        Long userId = signService.signup(sIgnUpRequest);
        return responseService.getSingleResult(userService.findById(userId));
    }

    @GetMapping("/{userId}")
    public SingleResponse<UserResponse> getUserInfo(@PathVariable Long userId){
        return responseService.getSingleResult(userService.findById(userId));
    }
}
