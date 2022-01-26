package walkbook.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import walkbook.server.advice.exception.CLoginFailedException;
import walkbook.server.advice.exception.CSignupFailedException;
import walkbook.server.domain.User;
import walkbook.server.dto.sign.SignUpRequest;
import walkbook.server.dto.sign.SignInRequest;
import walkbook.server.jwt.JwtTokenUtil;
import walkbook.server.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public String signin(SignInRequest signInRequest) {
        User user = userRepository.findByUsername(signInRequest.getUsername()).orElseThrow(CLoginFailedException::new);

        if (!passwordEncoder.matches(signInRequest.getPassword(), user.getPassword()))
            throw new CLoginFailedException();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtTokenUtil.generateToken(authentication);
    }

    @Transactional
    public Long signup(SignUpRequest sIgnUpRequest) {
        if (userRepository.findByUsername(sIgnUpRequest.getUsername()).isPresent()) throw new CSignupFailedException();
        return userRepository.save(sIgnUpRequest.toEntity(passwordEncoder)).getUserId();
    }
}