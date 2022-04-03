package walkbook.server.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import walkbook.server.domain.PostComment;
import walkbook.server.domain.User;
import walkbook.server.dto.post.PostResponse;
import walkbook.server.dto.post.PostCommentRequest;
import walkbook.server.dto.post.PostRequest;
import walkbook.server.dto.post.PostDetailResponse;

import java.util.List;

public interface PostService {
    Page<PostResponse> getAllPosts(UserDetails requestUser, Pageable pageRequest);
    Page<PostResponse> getPostsByKeyword(UserDetails requestUser, String keyword, Pageable pageRequest);
    PostDetailResponse getPostById(UserDetails requestUser, Long postId);
    List<PostResponse> getPostsByUser(User user);
    List<PostResponse> getLikePostsByUser(User user);

    PostDetailResponse savePost(UserDetails requestUser, PostRequest postRequest);
    PostDetailResponse editPost(UserDetails requestUser, Long postId, PostRequest postRequest);
    void deletePost(UserDetails requestUser, Long postId);
    void likePost(UserDetails requestUser, Long postId);

    PostComment saveComment(UserDetails requestUser, Long postId, PostCommentRequest postCommentRequest);
    PostComment editComment(UserDetails requestUser, Long postId, Long commentId, PostCommentRequest postCommentRequest);
    void deleteComment(UserDetails requestUser, Long postId, Long commentId);
}
