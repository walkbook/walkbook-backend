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
                .location("????????? ?????????")
                .introduction("?????????")
                .build());
        userId = Math.toIntExact(user.getUserId());
    }

    @AfterEach
    public void Clear() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("???????????? ??????")
    public void signup_success() throws Exception {
        //given
        String userRequest = getUserRequest("tester", "tester", "tester", Gender.M, 20, "????????? ?????????", "?????????");
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
    @DisplayName("???????????? ??????, ?????? ??????")
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
    @DisplayName("????????? ??????")
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
    @DisplayName("????????? ??????")
    public void signin_fail() throws Exception {
        //given
        String object = getUserRequest("tester", "tester", "tester", Gender.M, 20, "????????? ?????????", "?????????");

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
    @DisplayName("?????????????????? ??????")
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
    @DisplayName("?????????????????? ??????, ???????????? ?????? ??????id")
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
    @DisplayName("?????????????????? ??????")
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

        String userRequest = getUserRequest("tester", "tester", "tester", Gender.M, 20, "????????? ?????????", "?????????");

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
                .andExpect(jsonPath("$.data.location").value("????????? ?????????"))
                .andExpect(jsonPath("$.data.introduction").value("?????????"));
    }

    @Test
    @DisplayName("?????????????????? ??????, ?????? ??????")
    public void editUser_fail_unauthorized() throws Exception {
        //given
        String userRequest = getUserRequest("tester", "tester", "tester", Gender.M, 20, "????????? ?????????", "?????????");

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
