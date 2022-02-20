package walkbook.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import walkbook.server.dto.post.PostResponse;
import walkbook.server.dto.user.TokenResponse;
import walkbook.server.dto.user.UserRequest;
import walkbook.server.dto.user.UserResponse;
import walkbook.server.dto.CommonResponse;
import walkbook.server.dto.SingleResponse;
import walkbook.server.service.SignService;
import walkbook.server.service.UserService;
import walkbook.server.service.response.ResponseService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final ResponseService responseService;
    private final SignService signService;
    private final UserService userService;

    @PostMapping("/signin")
    public TokenResponse createAuthenticationToken(@RequestBody UserRequest userRequest) {
        String username = userRequest.getUsername();
        final String token = signService.signin(userRequest);
        return responseService.getTokenResult(new UserResponse(userService.findByUsername(username)), token);
    }

    @PostMapping("/signup")
    public CommonResponse registerUser(@RequestBody UserRequest userRequest) {
        Long userId = signService.signup(userRequest);
        return responseService.getSingleResult(new UserResponse(userService.findById(userId)));
    }

    @GetMapping("/{userId}")
    public SingleResponse<UserResponse> getUserInfo(@PathVariable Long userId) {
        return responseService.getSingleResult(new UserResponse(userService.findById(userId)));
    }

//    @GetMapping("/{userId}/liked-post")
//    public SingleResponse<PostResponse> getUserLikePosts(@PathVariable Long userId){
//        return responseService.getListResult();
//    }
//
//    @GetMapping("/{userId}/my-post")
//    public SingleResponse<PostResponse> getUserPosts(@PathVariable Long userId){
//        return responseService.getListResult();
//    }
}
