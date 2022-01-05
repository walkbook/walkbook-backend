package walkbook.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import walkbook.server.domain.User;
import walkbook.server.dto.user.UserResponse;
import walkbook.server.jwt.JwtTokenUtil;
import walkbook.server.dto.sign.SignInRequest;
import walkbook.server.dto.sign.SIgnUpRequest;
import walkbook.server.model.response.CommonResult;
import walkbook.server.model.response.SingleResult;
import walkbook.server.repository.UserRepository;
import walkbook.server.service.SignService;
import walkbook.server.service.UserService;
import walkbook.server.service.response.ResponseService;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/user")
public class JwtAuthenticationController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    ResponseService responseService;
    @Autowired
    SignService signService;
    @Autowired
    UserService userService;

    @PostMapping("/signin")
    public SingleResult<String> createAuthenticationToken(@Valid @RequestBody SignInRequest signInRequest) {
        final String token = signService.signin(signInRequest);
        return responseService.getSingleResult(token);
    }

    @PostMapping("/signup")
    public CommonResult registerUser(@Valid @RequestBody SIgnUpRequest sIgnUpRequest) {
        Long signupId = signService.signup(sIgnUpRequest);
        return responseService.getSingleResult(signupId);
    }

    @GetMapping("/{userId}")
    public SingleResult<UserResponse> getUserInfo(@PathVariable Long userId){
        return responseService.getSingleResult(userService.findById(userId));
    }
}
