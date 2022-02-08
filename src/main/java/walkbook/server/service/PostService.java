package walkbook.server.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import walkbook.server.advice.exception.CPostNotFoundException;
import walkbook.server.domain.Post;
import walkbook.server.domain.PostLike;
import walkbook.server.domain.User;
import walkbook.server.dto.post.PageResponse;
import walkbook.server.dto.post.PostLikeResponse;
import walkbook.server.dto.post.PostRequest;
import walkbook.server.jwt.JwtTokenUtil;
import walkbook.server.repository.PostLikeRepository;
import walkbook.server.repository.PostRepository;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.atomic.AtomicReference;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;

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
    public Post savePost(ServletRequest request, PostRequest postRequest) {
        String token = jwtTokenUtil.resolveToken((HttpServletRequest) request);
        Post post = postRequest.toEntity();
        post.setUser(userService.findByUsername(jwtTokenUtil.getUsernameFromToken(token)));
        postRepository.save(post);
        return post;
    }

    @Transactional(readOnly = true)
    public Post getPostByPostId(Long postId) {
        return postRepository.findAllByPostId(postId);
    }

    @Transactional
    public Post editPost(Long postId, PostRequest postRequest) {
        Post post = postRepository.findAllByPostId(postId);
        post.setTitle(postRequest.getTitle());
        post.setDescription(postRequest.getDescription());
        post.setStartLocation(postRequest.getStartLocation());
        post.setFinishLocation(postRequest.getFinishLocation());
        post.setTmi(postRequest.getTmi());
        return post;
    }

    @Transactional
    public void deletePost(Long postId) {
        postRepository.findById(postId).orElseThrow(CPostNotFoundException::new);
        postRepository.deleteById(postId);
    }

    @Transactional
    public PostLikeResponse likePost(ServletRequest request, Long postId) {
        String token = jwtTokenUtil.resolveToken((HttpServletRequest) request);
        Post post = postRepository.findById(postId).orElseThrow(CPostNotFoundException::new);
        User user = userService.findByUsername(jwtTokenUtil.getUsernameFromToken(token));
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
}
