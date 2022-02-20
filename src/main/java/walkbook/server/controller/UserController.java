package walkbook.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import walkbook.server.dto.ListResponse;
import walkbook.server.dto.post.PostResponse;
import walkbook.server.dto.user.TokenResponse;
import walkbook.server.dto.user.UserRequest;
import walkbook.server.dto.user.UserResponse;
import walkbook.server.dto.CommonResponse;
import walkbook.server.dto.SingleResponse;
import walkbook.server.service.PostService;
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
    private final PostService postService;

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

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{userId}")
    public SingleResponse<UserResponse> editUserInfo(@AuthenticationPrincipal UserDetails user, @PathVariable Long userId, @RequestBody UserRequest userRequest) {
        return responseService.getSingleResult(new UserResponse(userService.update(user, userId, userRequest)));
    }

    @GetMapping("/{userId}/post")
    public ListResponse<PostResponse> getUserPosts(@PathVariable Long userId){
        return responseService.getListResult(postService.getPostByUser(userService.findById(userId)));
    }
    @GetMapping("/{userId}/liked-post")
    public ListResponse<PostResponse> getUserLikePosts(@PathVariable Long userId){
        return responseService.getListResult(postService.getLikePostByUser(userService.findById(userId)));
    }
}
