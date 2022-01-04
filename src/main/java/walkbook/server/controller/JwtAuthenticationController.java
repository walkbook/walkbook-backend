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
import walkbook.server.service.response.ResponseService;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api")
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

    @PostMapping("/signin")
    public SingleResult<String> createAuthenticationToken(@Valid @RequestBody SignInRequest signInRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.getUsername(),
                        signInRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = jwtTokenUtil.generateToken(authentication);
        return responseService.getSingleResult(token);
    }

    @PostMapping("/signup")
    public CommonResult registerUser(@Valid @RequestBody SIgnUpRequest sIgnUpRequest) {
        if(userRepository.existsByUsername(sIgnUpRequest.getUsername())) {
            return responseService.getFailResult(-1, "Username is already taken!");
        }
        User user = User.builder()
                .username(sIgnUpRequest.getUsername())
                .password(passwordEncoder.encode(sIgnUpRequest.getPassword()))
                .nickname(sIgnUpRequest.getNickname())
                .gender(sIgnUpRequest.getGender())
                .age(sIgnUpRequest.getAge())
                .location(sIgnUpRequest.getLocation())
                .introduction(sIgnUpRequest.getIntroduction())
                .build();

        Long signupId = userRepository.save(user).getUserId();
        return responseService.getSingleResult(signupId);
    }

    @GetMapping("/user/{userId}")
    public SingleResult<UserResponse> getUserInfo(@PathVariable Long userId){
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()){
            UserResponse userResponse = UserResponse.builder()
                    .userId(user.get().getUserId())
                    .username(user.get().getUsername())
                    .nickname(user.get().getNickname())
                    .gender(user.get().getGender())
                    .age(user.get().getAge())
                    .location(user.get().getLocation())
                    .introduction(user.get().getIntroduction())
                    .build();
            return responseService.getSingleResult(userResponse);
        }
        else throw new RuntimeException();
    }
}
