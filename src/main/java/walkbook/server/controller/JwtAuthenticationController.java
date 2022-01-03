package walkbook.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import walkbook.server.jwt.JwtTokenUtil;
import walkbook.server.payload.LoginRequest;
import walkbook.server.payload.TokenResponse;
import walkbook.server.service.JwtUserDetailsService;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class JwtAuthenticationController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @PostMapping("/login")
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
}
