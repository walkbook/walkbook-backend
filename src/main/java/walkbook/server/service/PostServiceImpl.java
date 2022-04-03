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

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService{
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostCommentRepository postCommentRepository;
    private final UserService userServiceImpl;

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getAllPosts(UserDetails requestUser, Pageable pageRequest) {
        Page<Post> postList = postRepository.findAll(pageRequest);
        return getPageResponses(requestUser, postList);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getPostsByKeyword(UserDetails requestUser, String keyword, Pageable pageRequest) {
        Page<Post> postList;
        postList = postRepository.findByTitleContainingOrDescriptionContaining(pageRequest, keyword, keyword);
        return getPageResponses(requestUser, postList);
    }

    @Override
    @Transactional(readOnly = true)
    public PostDetailResponse getPostById(UserDetails requestUser, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(CPostNotFoundException::new);
        return getPostDetailResponseByRequestUser(requestUser, post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUser(User user) {
        List<Post> postList = postRepository.findAllByUser(user);
        return postList.stream().map(post -> getPostResponseByUser(user, post)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getLikePostsByUser(User user) {
        List<PostLike> postLikeList = postLikeRepository.findAllByUser(user);
        return postLikeList.stream().map(
                        postLike -> getPostResponseByUser(user,
                                postRepository.findById(postLike.getPost().getPostId()).orElseThrow(CPostNotFoundException::new)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PostDetailResponse savePost(UserDetails requestUser, PostRequest postRequest) {
        Post post = postRequest.toEntity();
        User user = userServiceImpl.findByUsername(requestUser.getUsername());
        post.setUser(user);
        postRepository.save(post);
        return new PostDetailResponse(post);
    }

    @Override
    @Transactional
    public PostDetailResponse editPost(UserDetails requestUser, Long postId, PostRequest postRequest) {
        Post post = postRepository.findById(postId).orElseThrow(CPostNotFoundException::new);
        checkSameUser(requestUser, post.getUser().getUsername());
        post.set(postRequest);
        return getPostDetailResponseByRequestUser(requestUser, post);
    }

    @Override
    @Transactional
    public void deletePost(UserDetails requestUser, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(CPostNotFoundException::new);
        checkSameUser(requestUser, post.getUser().getUsername());
        postRepository.delete(post);
    }

    @Override
    @Transactional
    public void likePost(UserDetails requestUser, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(CPostNotFoundException::new);
        User user = userServiceImpl.findByUsername(requestUser.getUsername());
        postLikeRepository.findByPostAndUser(post, user).ifPresentOrElse(
                //좋아요 있을 경우 좋아요 삭제
                postLike -> {
                    postLikeRepository.delete(postLike);
                    post.removePostLike(postLike);
                },
                //좋아요 없을 경우 좋아요 추가
                () -> {
                    PostLike postLike = PostLike.builder().build();
                    postLike.mapping(user, post);
                    postLikeRepository.save(postLike);
                }
        );
    }

    @Override
    @Transactional
    public PostComment saveComment(UserDetails requestUser, Long postId, PostCommentRequest postCommentRequest) {
        PostComment postComment = postCommentRequest.toEntity();
        postComment.setUser(userServiceImpl.findByUsername(requestUser.getUsername()));
        Post post = postRepository.findById(postId).orElseThrow(CPostNotFoundException::new);
        postComment.setPost(post);
        post.mappingPostComment(postComment);
        postCommentRepository.save(postComment);
        return postComment;
    }

    @Override
    @Transactional
    public PostComment editComment(UserDetails requestUser, Long postId, Long commentId, PostCommentRequest postCommentRequest) {
        PostComment postComment = postCommentRepository.findById(commentId).orElseThrow(RuntimeException::new);
        checkSameUser(requestUser, postComment.getUser().getUsername());
        Post post = postRepository.findById(postId).orElseThrow(CPostNotFoundException::new);
        post.removePostComment(postComment);
        postComment.setContent(postCommentRequest.getContent());
        post.mappingPostComment(postComment);
        return postComment;
    }

    @Override
    @Transactional
    public void deleteComment(UserDetails requestUser, Long postId, Long commentId) {
        PostComment postComment = postCommentRepository.findById(commentId).orElseThrow(RuntimeException::new);
        checkSameUser(requestUser, postComment.getUser().getUsername());
        Post post = postRepository.findById(postId).orElseThrow(CPostNotFoundException::new);
        post.removePostComment(postComment);
        postCommentRepository.delete(postComment);
    }

    private Page<PostResponse> getPageResponses(UserDetails requestUser, Page<Post> postList) {
        return postList.map(
                post -> {
                    PostResponse postResponse = new PostResponse(post);
                    if (requestUser != null) {
                        User user = userServiceImpl.findByUsername(requestUser.getUsername());
                        if (isLiked(user, post)) {
                            postResponse.setLiked(true);
                        }
                    }
                    return postResponse;
                });
    }

    private PostDetailResponse getPostDetailResponseByRequestUser(UserDetails requestUser, Post post) {
        PostDetailResponse postDetailResponse = new PostDetailResponse(post);
        List<PostCommentResponse> postCommentList = post.getCommentList().stream().map(PostCommentResponse::fromEntity).collect(Collectors.toList());
        postDetailResponse.setComments(postCommentList);
        if (requestUser != null) {
            User user = userServiceImpl.findByUsername(requestUser.getUsername());
            if (isLiked(user, post)) {
                postDetailResponse.setLiked(true);
            }
        }
        return postDetailResponse;
    }

    private PostResponse getPostResponseByUser(User user, Post post) {
        PostResponse postcardResponse = new PostResponse(post);
        if (isLiked(user, post)) {
            postcardResponse.setLiked(true);
        }
        return postcardResponse;
    }

    private void checkSameUser(UserDetails requestUser, String username) {
        if (!requestUser.getUsername().equals(username)) {
            throw new AccessDeniedException("작성자와 수정자가 다릅니다.");
        }
    }

    private boolean isLiked(User user, Post post) {
        return postLikeRepository.findByPostAndUser(post, user).isPresent();
    }
}
