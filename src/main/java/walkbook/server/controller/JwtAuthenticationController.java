package walkbook.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import walkbook.server.domain.User;
import walkbook.server.jwt.JwtTokenUtil;
import walkbook.server.payload.ApiResponse;
import walkbook.server.payload.LoginRequest;
import walkbook.server.payload.SIgnUpRequest;
import walkbook.server.payload.TokenResponse;
import walkbook.server.repository.UserRepository;
import walkbook.server.service.JwtUserDetailsService;

import javax.validation.Valid;

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

    @PostMapping("/signin")
    public ResponseEntity<TokenResponse> createAuthenticationToken(@Valid @RequestBody LoginRequest loginRequest) throws Exception {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = jwtTokenUtil.generateToken(authentication);
        return ResponseEntity.ok(new TokenResponse(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SIgnUpRequest sIgnUpRequest) {
        if(userRepository.existsByUsername(sIgnUpRequest.getUsername())) {
            return new ResponseEntity(new ApiResponse(false, "Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }
        User user = User.builder()
                .username(sIgnUpRequest.getUsername())
                .password(sIgnUpRequest.getPassword())
                .nickname(sIgnUpRequest.getNickname())
                .gender(sIgnUpRequest.getGender())
                .age(sIgnUpRequest.getAge())
                .location(sIgnUpRequest.getLocation())
                .introduction(sIgnUpRequest.getIntroduction())
                .build();

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully!"));
    }
}
