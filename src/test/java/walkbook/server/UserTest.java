package walkbook.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import walkbook.server.domain.Gender;
import walkbook.server.domain.User;
import walkbook.server.dto.user.UserRequest;
import walkbook.server.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserTest {
    private static int userId;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void Init() {
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

    @AfterEach
    public void Clear() {
        userRepository.deleteAll();
    }

    @Test
    public void 회원가입_성공() throws Exception {
        //given
        String object = objectMapper.writeValueAsString(UserRequest.builder()
                .username("tester")
                .password(passwordEncoder.encode("tester"))
                .nickname("테스터")
                .gender(Gender.F)
                .age("25")
                .location("서울시 용산구")
                .introduction("테스트")
                .build());
        //when
        ResultActions actions = mockMvc.perform(post("/api/user/signup")
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
        String object = objectMapper.writeValueAsString(UserRequest.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .nickname("admin")
                .gender(Gender.F)
                .age("20")
                .location("서울시 강남구")
                .introduction("중복테스트")
                .build());
        //when
        ResultActions actions = mockMvc.perform(post("/api/user/signup")
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
    public void 로그인_성공() throws Exception {
        //given
        String object = objectMapper.writeValueAsString(UserRequest.builder()
                .username("admin")
                .password("admin")
                .build());

        //when
        ResultActions actions = mockMvc.perform(post("/api/user/signin")
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
    public void 로그인_실패() throws Exception {
        //given
        String object = objectMapper.writeValueAsString(UserRequest.builder()
                .username("tester")
                .password("tester")
                .build());

        //when
        ResultActions actions = mockMvc.perform(post("/api/user/signin")
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
    public void 회원정보조회_성공() throws Exception {
        //when
        ResultActions actions = mockMvc.perform(get("/api/user/{userId}", userId));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.nickname").value("admin"));
    }

    @Test
    public void 회원정보조회_실패_존재하지_않는_유저ID() throws Exception {
        //when
        ResultActions actions = mockMvc.perform(get("/api/user/{userId}", userId + 1));

        //then
        actions
                .andDo(print())
                .andExpect(status().isBadRequest())
                //-1000, This member does not exist.
                .andExpect(jsonPath("$.code").value("-1000"));
    }
}
