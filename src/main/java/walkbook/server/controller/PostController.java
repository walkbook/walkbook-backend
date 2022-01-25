package walkbook.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import walkbook.server.domain.Post;
import walkbook.server.domain.User;
import walkbook.server.dto.ListResponse;
import walkbook.server.dto.SingleResponse;
import walkbook.server.dto.post.PostRequest;
import walkbook.server.dto.post.PostResponse;
import walkbook.server.service.PostService;
import walkbook.server.service.UserService;
import walkbook.server.service.response.ResponseService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final ResponseService responseService;

    @GetMapping("/page")
    public Page<PostResponse> getAllPosts(@PageableDefault(size=8, sort="createdDate") Pageable pageRequest){
        return postService.getAllPosts(pageRequest);
    }

    @PostMapping("/{userId}")
    public SingleResponse<PostResponse> savePost(@PathVariable Long userId, @RequestBody PostRequest postRequest){
        User author = userService.findById(userId);
        Post newPost = postRequest.toEntity();
        newPost.setUser(author);
        postService.savePost(newPost);
        return responseService.getSingleResult(new PostResponse(newPost));
    }
}
