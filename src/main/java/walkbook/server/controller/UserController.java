package walkbook.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import walkbook.server.dto.CommonResponse;
import walkbook.server.dto.ListResponse;
import walkbook.server.dto.SingleResponse;
import walkbook.server.dto.post.PostResponse;
import walkbook.server.dto.user.TokenResponse;
import walkbook.server.dto.user.UserRequest;
import walkbook.server.dto.user.UserResponse;
import walkbook.server.service.PostService;
import walkbook.server.service.ResponseService;
import walkbook.server.service.SignService;
import walkbook.server.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final ResponseService responseServiceImpl;
    private final SignService signServiceImpl;
    private final UserService userServiceImpl;
    private final PostService postServiceImpl;

    @PostMapping("/signin")
    public TokenResponse<UserResponse> createAuthenticationToken(@RequestBody UserRequest userRequest) {
        String username = userRequest.getUsername();
        final String token = signServiceImpl.signin(userRequest);
        return responseServiceImpl.getTokenResult(new UserResponse(userServiceImpl.findByUsername(username)), token);
    }

    @PostMapping("/signup")
    public CommonResponse registerUser(@RequestBody UserRequest userRequest) {
        Long userId = signServiceImpl.signup(userRequest);
        return responseServiceImpl.getSingleResult(new UserResponse(userServiceImpl.findById(userId)));
    }

    @GetMapping("/{userId}")
    public SingleResponse<UserResponse> getUserInfo(@PathVariable Long userId) {
        return responseServiceImpl.getSingleResult(new UserResponse(userServiceImpl.findById(userId)));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{userId}")
    public SingleResponse<UserResponse> editUserInfo(@AuthenticationPrincipal UserDetails user, @PathVariable Long userId, @RequestBody UserRequest userRequest) {
        return responseServiceImpl.getSingleResult(new UserResponse(userServiceImpl.update(user, userId, userRequest)));
    }

    @GetMapping("/{userId}/post")
    public ListResponse<PostResponse> getUserPosts(@PathVariable Long userId){
        return responseServiceImpl.getListResult(postServiceImpl.getPostsByUser(userServiceImpl.findById(userId)));
    }
    @GetMapping("/{userId}/liked-post")
    public ListResponse<PostResponse> getUserLikePosts(@PathVariable Long userId){
        return responseServiceImpl.getListResult(postServiceImpl.getLikePostsByUser(userServiceImpl.findById(userId)));
    }
}
