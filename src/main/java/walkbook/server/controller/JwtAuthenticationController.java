package walkbook.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
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
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> createAuthenticationToken(@Valid @RequestBody LoginRequest loginRequest) throws Exception {
        final UserDetails userDetails = jwtUserDetailsService
                .loadUserByUsername(loginRequest.getUsername());

        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new TokenResponse(token));
    }
}
