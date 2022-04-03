package walkbook.server.service;

import lombok.RequiredArgsConstructor;
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
import walkbook.server.dto.user.UserRequest;
import walkbook.server.jwt.JwtTokenUtil;
import walkbook.server.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class SignServiceImpl implements SignService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public String signin(UserRequest userRequest) {
        User user = userRepository.findByUsername(userRequest.getUsername()).orElseThrow(CLoginFailedException::new);

        if (!passwordEncoder.matches(userRequest.getPassword(), user.getPassword()))
            throw new CLoginFailedException();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRequest.getUsername(), userRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtTokenUtil.generateToken(authentication);
    }

    @Override
    @Transactional
    public Long signup(UserRequest userRequest) {
        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) throw new CSignupFailedException();
        return userRepository.save(userRequest.toEntity(passwordEncoder)).getUserId();
    }
}