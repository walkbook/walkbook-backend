package walkbook.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import walkbook.server.domain.Post;
import walkbook.server.dto.CommonResponse;
import walkbook.server.dto.SingleResponse;
import walkbook.server.dto.post.PageResponse;
import walkbook.server.dto.post.PostRequest;
import walkbook.server.dto.post.PostResponse;
import walkbook.server.service.PostService;
import walkbook.server.service.response.ResponseService;

import javax.servlet.ServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {
    private final PostService postService;
    private final ResponseService responseService;

    @GetMapping("/page")
    public Page<PageResponse> getAllPosts(@PageableDefault(size = 8, sort = "createdDate") Pageable pageRequest) {
        return postService.getAllPosts(pageRequest);
    }

    @PostMapping("/create")
    public SingleResponse<PostResponse> savePost(ServletRequest request, @RequestBody PostRequest postRequest) {
        Post newPost = postService.savePost(request, postRequest);
        return responseService.getSingleResult(new PostResponse(newPost));
    }

    @GetMapping("/{postId}")
    public SingleResponse<PostResponse> getPost(@PathVariable Long postId) {
        return responseService.getSingleResult(new PostResponse(postService.getPostByPostId(postId)));
    }

    @PutMapping("/{postId}/edit")
    public SingleResponse<PostResponse> editPost(@PathVariable Long postId, @RequestBody PostRequest postRequest) {
        return responseService.getSingleResult(new PostResponse(postService.editPost(postId, postRequest)));
    }

    @DeleteMapping("/{postId}/delete")
    public CommonResponse deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return responseService.getSuccessResult();
    }
}
