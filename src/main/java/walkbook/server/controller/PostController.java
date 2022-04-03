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
import walkbook.server.service.ResponseService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {
    private final PostService postServiceImpl;
    private final ResponseService responseServiceImpl;

    @GetMapping("/page")
    public Page<PostResponse> getAllPosts(@AuthenticationPrincipal UserDetails user, @PageableDefault(size = 8, sort = "createdDate,desc") Pageable pageRequest) {
        return postServiceImpl.getAllPosts(user, pageRequest);
    }

    @GetMapping("/search")
    public Page<PostResponse> searchPosts(@AuthenticationPrincipal UserDetails user,
                                          @RequestParam(value = "keyword") String keyword,
                                          @PageableDefault(size = 8, sort = "createdDate") Pageable pageRequest) {
        return postServiceImpl.getPostsByKeyword(user, keyword, pageRequest);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("")
    public SingleResponse<PostDetailResponse> savePost(@AuthenticationPrincipal UserDetails user, @RequestBody PostRequest postRequest) {
        return responseServiceImpl.getSingleResult(postServiceImpl.savePost(user, postRequest));
    }

    @GetMapping("/{postId}")
    public SingleResponse<PostDetailResponse> getPost(@AuthenticationPrincipal UserDetails user, @PathVariable Long postId) {
        return responseServiceImpl.getSingleResult(postServiceImpl.getPostById(user, postId));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{postId}")
    public SingleResponse<PostDetailResponse> editPost(@AuthenticationPrincipal UserDetails user, @PathVariable Long postId, @RequestBody PostRequest postRequest) {
        return responseServiceImpl.getSingleResult(postServiceImpl.editPost(user, postId, postRequest));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{postId}")
    public CommonResponse deletePost(@AuthenticationPrincipal UserDetails user, @PathVariable Long postId) {
        postServiceImpl.deletePost(user, postId);
        return responseServiceImpl.getSuccessResult();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{postId}/like")
    public CommonResponse likePost(@AuthenticationPrincipal UserDetails user, @PathVariable Long postId) {
        postServiceImpl.likePost(user, postId);
        return responseServiceImpl.getSuccessResult();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{postId}/comment")
    public SingleResponse<PostCommentResponse> saveComment(@AuthenticationPrincipal UserDetails user, @PathVariable Long postId, @RequestBody PostCommentRequest postCommentRequest) {
        return responseServiceImpl.getSingleResult(new PostCommentResponse(postServiceImpl.saveComment(user, postId, postCommentRequest)));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{postId}/comment/{commentId}")
    public SingleResponse<PostCommentResponse> editComment(@AuthenticationPrincipal UserDetails user, @PathVariable Long postId, @PathVariable Long commentId, @RequestBody PostCommentRequest postCommentRequest){
        return  responseServiceImpl.getSingleResult(new PostCommentResponse(postServiceImpl.editComment(user, postId, commentId, postCommentRequest)));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{postId}/comment/{commentId}")
    public CommonResponse deleteComment(@AuthenticationPrincipal UserDetails user, @PathVariable Long postId, @PathVariable Long commentId){
        postServiceImpl.deleteComment(user, postId, commentId);
        return responseServiceImpl.getSuccessResult();
    }
}
