package walkbook.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import walkbook.server.dto.CommonResponse;
import walkbook.server.dto.SingleResponse;
import walkbook.server.dto.post.*;
import walkbook.server.service.PostService;
import walkbook.server.service.response.ResponseService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {
    private final PostService postService;
    private final ResponseService responseService;

    @GetMapping("/page")
    public Page<PostCardResponse> getAllPosts(@AuthenticationPrincipal UserDetails user, @PageableDefault(size = 8, sort = "createdDate,desc") Pageable pageRequest) {
        return postService.getAllPosts(user, pageRequest);
    }

    @GetMapping("/search")
    public Page<PostCardResponse> searchPosts(@AuthenticationPrincipal UserDetails user,
                                              @RequestParam(value = "keyword") String keyword,
                                              @PageableDefault(size = 8, sort = "createdDate") Pageable pageRequest) {
        return postService.getPostsByKeyword(user, keyword, pageRequest);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("")
    public SingleResponse<PostResponse> savePost(@AuthenticationPrincipal UserDetails user, @RequestBody PostRequest postRequest) {
        return responseService.getSingleResult(postService.savePost(user, postRequest));
    }

    @GetMapping("/{postId}")
    public SingleResponse<PostResponse> getPost(@AuthenticationPrincipal UserDetails user, @PathVariable Long postId) {
        return responseService.getSingleResult(postService.getPostById(user, postId));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{postId}")
    public SingleResponse<PostResponse> editPost(@AuthenticationPrincipal UserDetails user, @PathVariable Long postId, @RequestBody PostRequest postRequest) {
        return responseService.getSingleResult(postService.editPost(user, postId, postRequest));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{postId}")
    public CommonResponse deletePost(@AuthenticationPrincipal UserDetails user, @PathVariable Long postId) {
        postService.deletePost(user, postId);
        return responseService.getSuccessResult();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{postId}/like")
    public CommonResponse likePost(@AuthenticationPrincipal UserDetails user, @PathVariable Long postId) {
        postService.likePost(user, postId);
        return responseService.getSuccessResult();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{postId}/comment")
    public SingleResponse<PostCommentResponse> saveComment(@AuthenticationPrincipal UserDetails user, @PathVariable Long postId, @RequestBody PostCommentRequest postCommentRequest) {
        return responseService.getSingleResult(new PostCommentResponse(postService.saveComment(user, postId, postCommentRequest)));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{postId}/comment/{commentId}")
    public SingleResponse<PostCommentResponse> editComment(@AuthenticationPrincipal UserDetails user, @PathVariable Long postId, @PathVariable Long commentId, @RequestBody PostCommentRequest postCommentRequest){
        return  responseService.getSingleResult(new PostCommentResponse(postService.editComment(user, postId, commentId, postCommentRequest)));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{postId}/comment/{commentId}")
    public CommonResponse deleteComment(@AuthenticationPrincipal UserDetails user, @PathVariable Long postId, @PathVariable Long commentId){
        postService.deleteComment(user, postId, commentId);
        return responseService.getSuccessResult();
    }
}
