package walkbook.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import walkbook.server.domain.User;
import walkbook.server.enums.Gender;
import walkbook.server.payload.LoginRequest;
import walkbook.server.payload.SIgnUpRequest;
import walkbook.server.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @BeforeEach
    public void setUp() {
        userRepository.save(User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .nickname("admin")
                .gender(Gender.M)
                .age("20")
                .location("서울시 관악구")
                .introduction("안녕하세요. 테스트입니다.")
                .build());
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
        String object = objectMapper.writeValueAsString(LoginRequest.builder()
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
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    public void 로그인_실패() throws Exception{
        //given
        String object = objectMapper.writeValueAsString(LoginRequest.builder()
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
                .andExpect(status().isForbidden());
    }

}
