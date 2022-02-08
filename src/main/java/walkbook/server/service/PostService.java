package walkbook.server.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import walkbook.server.advice.exception.CPostNotFoundException;
import walkbook.server.domain.Post;
import walkbook.server.domain.PostComment;
import walkbook.server.domain.PostLike;
import walkbook.server.domain.User;
import walkbook.server.dto.post.*;
import walkbook.server.repository.PostCommentRepository;
import walkbook.server.repository.PostLikeRepository;
import walkbook.server.repository.PostRepository;

import java.util.concurrent.atomic.AtomicReference;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostCommentRepository postCommentRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public Page<PageResponse> getAllPosts(Pageable pageRequest) {
        Page<Post> postList = postRepository.findAll(pageRequest);
        return postList.map(PageResponse::new);
    }

    @Transactional(readOnly = true)
    public Page<PageResponse> searchPosts(String searchType, String keyword, Pageable pageRequest) {
        Page<Post> postList;
        if (searchType.equals("title")) {
            postList = postRepository.findByTitleContaining(pageRequest, keyword);
        } else {
            postList = postRepository.findByDescriptionContaining(pageRequest, keyword);
        }
        return postList.map(PageResponse::new);
    }

    @Transactional
    public Post savePost(UserDetails requestUser, PostRequest postRequest) {
        Post post = postRequest.toEntity();
        post.setUser(userService.findByUsername(requestUser.getUsername()));
        postRepository.save(post);
        return post;
    }

    @Transactional(readOnly = true)
    public Post getPostByPostId(Long postId) {
        return postRepository.findAllByPostId(postId);
    }

    @Transactional
    public Post editPost(UserDetails requestUser, Long postId, PostRequest postRequest) {
        Post post = postRepository.findById(postId).orElseThrow(CPostNotFoundException::new);
        checkSameUser(requestUser, post.getUser().getUsername());
        post.setTitle(postRequest.getTitle());
        post.setDescription(postRequest.getDescription());
        post.setStartLocation(postRequest.getStartLocation());
        post.setFinishLocation(postRequest.getFinishLocation());
        post.setTmi(postRequest.getTmi());
        return post;
    }

    @Transactional
    public void deletePost(UserDetails requestUser, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(CPostNotFoundException::new);
        checkSameUser(requestUser, post.getUser().getUsername());
        postRepository.delete(post);
    }

    @Transactional
    public PostLikeResponse likePost(UserDetails requestUser, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(CPostNotFoundException::new);
        User user = userService.findByUsername(requestUser.getUsername());
        AtomicReference<Boolean> liked = new AtomicReference<>(false);
        postLikeRepository.findByPostAndUser(post, user).ifPresentOrElse(
                //좋아요 있을 경우 좋아요 삭제
                postLike -> {
                    postLikeRepository.delete(postLike);
                    post.removePostLike(postLike);
                    user.removePostLike(postLike);
                },
                //좋아요 없을 경우 좋아요 추가
                () -> {
                    PostLike postLike = PostLike.builder().build();
                    postLike.mappingPost(post);
                    postLike.mappingUser(user);
                    postLikeRepository.save(postLike);
                    liked.set(true);
                }
        );
        return new PostLikeResponse(post, user, liked.get());
    }

    @Transactional
    public PostComment saveComment(UserDetails requestUser, Long postId, PostCommentRequest postCommentRequest){
        PostComment postComment = postCommentRequest.toEntity();
        postComment.setUser(userService.findByUsername(requestUser.getUsername()));
        Post post = postRepository.findById(postId).orElseThrow(CPostNotFoundException::new);
        postComment.setPost(post);
        postCommentRepository.save(postComment);
        post.mappingPostComment(postComment);
        return postComment;
    }

    @Transactional
    public PostComment editComment(UserDetails requestUser, Long postId, Long commentId, PostCommentRequest postCommentRequest){
        PostComment postComment = postCommentRepository.findById(commentId).orElseThrow(RuntimeException::new);
        checkSameUser(requestUser, postComment.getUser().getUsername());
        Post post = postRepository.findById(postId).orElseThrow(CPostNotFoundException::new);
        post.removePostComment(postComment);
        postComment.setContent(postCommentRequest.getContent());
        post.mappingPostComment(postComment);
        return postComment;
    }

    @Transactional
    public void deleteComment(UserDetails requestUser, Long postId, Long commentId){
        PostComment postComment = postCommentRepository.findById(commentId).orElseThrow(RuntimeException::new);
        checkSameUser(requestUser, postComment.getUser().getUsername());
        Post post = postRepository.findById(postId).orElseThrow(CPostNotFoundException::new);
        post.removePostComment(postComment);
        postCommentRepository.delete(postComment);
    }

    private void checkSameUser(UserDetails requestUser, String username) {
        if(!requestUser.getUsername().equals(username)){
            throw new AccessDeniedException("작성자와 수정자가 다릅니다.");
        }
    }
}
