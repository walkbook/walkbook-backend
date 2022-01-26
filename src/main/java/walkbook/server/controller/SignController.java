package walkbook.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import walkbook.server.dto.sign.TokenResponse;
import walkbook.server.dto.user.UserResponse;
import walkbook.server.dto.sign.SignInRequest;
import walkbook.server.dto.sign.SignUpRequest;
import walkbook.server.dto.CommonResponse;
import walkbook.server.dto.SingleResponse;
import walkbook.server.service.SignService;
import walkbook.server.service.UserService;
import walkbook.server.service.response.ResponseService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class SignController {
    private final ResponseService responseService;
    private final SignService signService;
    private final UserService userService;

    @PostMapping("/signin")
    public TokenResponse createAuthenticationToken(@Valid @RequestBody SignInRequest signInRequest) {
        String username = signInRequest.getUsername();
        final String token = signService.signin(signInRequest);
        return responseService.getTokenResult(userService.findByUsername(username), token);
    }

    @PostMapping("/signup")
    public CommonResponse registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        Long userId = signService.signup(signUpRequest);
        return responseService.getSingleResult(new UserResponse(userService.findById(userId)));
    }

    @GetMapping("/{userId}")
    public SingleResponse<UserResponse> getUserInfo(@PathVariable Long userId) {
        return responseService.getSingleResult(new UserResponse(userService.findById(userId)));
    }
}
