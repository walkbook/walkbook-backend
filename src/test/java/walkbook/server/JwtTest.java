package walkbook.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import walkbook.server.domain.User;
import walkbook.server.domain.Gender;
import walkbook.server.dto.sign.SignInRequest;
import walkbook.server.dto.sign.SIgnUpRequest;
import walkbook.server.jwt.JwtTokenUtil;
import walkbook.server.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class JwtTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    private static int userId;

    @BeforeEach
    public void setUp() {
        User user = userRepository.save(User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .nickname("admin")
                        .gender(Gender.M)
                        .age("20")
                        .location("서울시 관악구")
                        .introduction("안녕하세요. 테스트입니다.")
                        .build());
        userId = Math.toIntExact(user.getUserId());
    }

    @Test
    public void 회원가입_성공() throws Exception {
        //given
        String object = objectMapper.writeValueAsString(SIgnUpRequest.builder()
                .username("tester")
                .password(passwordEncoder.encode("tester"))
                .nickname("테스터")
                .gender(Gender.F)
                .age("25")
                .location("서울시 용산구")
                .introduction("테스트")
                .build());
        //when
        ResultActions actions = mockMvc.perform(post("/api/signup")
                .content(object)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void 회원가입_실패_중복회원() throws Exception {
        //given
        String object = objectMapper.writeValueAsString(SIgnUpRequest.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .nickname("admin")
                .gender(Gender.F)
                .age("20")
                .location("서울시 강남구")
                .introduction("중복테스트")
                .build());
        //when
        ResultActions actions = mockMvc.perform(post("/api/signup")
                .content(object)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    public void 로그인_성공() throws Exception{
        //given
        String object = objectMapper.writeValueAsString(SignInRequest.builder()
                .username("admin")
                .password("admin")
                .build());

        //when
        ResultActions actions = mockMvc.perform(post("/api/signin")
                .content(object)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void 로그인_실패() throws Exception{
        //given
        String object = objectMapper.writeValueAsString(SignInRequest.builder()
                .username("tester")
                .password("tester")
                .build());

        //when
        ResultActions actions = mockMvc.perform(post("/api/signin")
                .content(object)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.code").value("-1001"));
    }

    @Test
    public void 회원정보조회_유저ID_성공() throws Exception {
        //given
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        "admin",
                        "admin"
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = jwtTokenUtil.generateToken(authentication);

        //when
        ResultActions actions = mockMvc.perform(get("/api/user/{userId}", userId)
                .header("X-AUTH-TOKEN", token));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.nickname").value("admin"));
    }

    @Test
    public void 회원정보조회_유저ID_실패() throws Exception{
        //when
        ResultActions actions = mockMvc.perform(get("/api/user/{userId}", userId));

        //then
        actions
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
