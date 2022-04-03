package walkbook.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import walkbook.server.advice.exception.CLoginFailedException;
import walkbook.server.advice.exception.CSignupFailedException;
import walkbook.server.advice.exception.CUserNotFoundException;
import walkbook.server.domain.Gender;
import walkbook.server.domain.User;
import walkbook.server.dto.user.UserRequest;
import walkbook.server.jwt.JwtTokenUtil;
import walkbook.server.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    private static int userId;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @BeforeEach
    public void Init() {
        User user = userRepository.save(User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .nickname("admin")
                .gender(Gender.M)
                .age(20)
                .location("서울시 관악구")
                .introduction("초기값")
                .build());
        userId = Math.toIntExact(user.getUserId());
    }

    @AfterEach
    public void Clear() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 성공")
    public void signup_success() throws Exception {
        //given
        String userRequest = getUserRequest("tester", "tester", "tester", Gender.M, 20, "서울시 용산구", "테스트");
        //when
        ResultActions actions = mockMvc.perform(post("/api/user/signup")
                .content(userRequest)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("회원가입 실패, 중복 회원")
    public void signup_fail_duplicateUser() throws Exception {
        //given
        String userRequest = getUserRequest("admin", "admin", "", Gender.M, 20, "", "");
        //when
        ResultActions actions = mockMvc.perform(post("/api/user/signup")
                .content(userRequest)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(CSignupFailedException.class)))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("로그인 성공")
    public void signin_success() throws Exception {
        //given
        String userRequest = getUserRequest("admin", "admin", "", Gender.M, 20, "", "");

        //when
        ResultActions actions = mockMvc.perform(post("/api/user/signin")
                .content(userRequest)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("로그인 실패")
    public void signin_fail() throws Exception {
        //given
        String object = getUserRequest("tester", "tester", "tester", Gender.M, 20, "서울시 용산구", "테스트");

        //when
        ResultActions actions = mockMvc.perform(post("/api/user/signin")
                .content(object)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(CLoginFailedException.class)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("회원정보조회 성공")
    public void getUser_success() throws Exception {
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
    @DisplayName("회원정보조회 실패, 존재하지 않는 유저id")
    public void getUser_fail_userNotFound() throws Exception {
        //when
        ResultActions actions = mockMvc.perform(get("/api/user/{userId}", userId + 1));

        //then
        actions
                .andDo(print())
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(CUserNotFoundException.class)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("회원정보수정 성공")
    public void editUser_success() throws Exception {
        //given
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        "admin",
                        "admin"
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = jwtTokenUtil.generateToken(authentication);

        String userRequest = getUserRequest("tester", "tester", "tester", Gender.M, 20, "서울시 용산구", "테스트");

        //when
        ResultActions actions = mockMvc.perform(put("/api/user/{userId}", userId)
                .header("X-AUTH-TOKEN", token)
                .content(userRequest)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.data.nickname").value("tester"))
                .andExpect(jsonPath("$.data.location").value("서울시 용산구"))
                .andExpect(jsonPath("$.data.introduction").value("테스트"));
    }

    @Test
    @DisplayName("회원정보수정 실패, 권한 없음")
    public void editUser_fail_unauthorized() throws Exception {
        //given
        String userRequest = getUserRequest("tester", "tester", "tester", Gender.M, 20, "서울시 용산구", "테스트");

        //when
        ResultActions actions = mockMvc.perform(put("/api/user/{userId}", userId)
                .content(userRequest)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    private String getUserRequest(String username, String password, String nickname, Gender gender, int age, String location, String introduction) throws JsonProcessingException {
        return objectMapper.writeValueAsString(UserRequest.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .gender(gender)
                .age(age)
                .location(location)
                .introduction(introduction)
                .build());
    }
}
