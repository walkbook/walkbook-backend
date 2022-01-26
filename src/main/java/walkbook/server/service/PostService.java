package walkbook.server.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import walkbook.server.domain.Post;
import walkbook.server.dto.post.PageResponse;
import walkbook.server.dto.post.PostRequest;
import walkbook.server.jwt.JwtTokenUtil;
import walkbook.server.repository.PostRepository;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;

    @Transactional(readOnly = true)
    public Page<PageResponse> getAllPosts(Pageable pageRequest) {
        Page<Post> postList = postRepository.findAll(pageRequest);
        return postList.map(PageResponse::new);
    }

    @Transactional
    public Post savePost(ServletRequest request, PostRequest postRequest) {
        String token = jwtTokenUtil.resolveToken((HttpServletRequest) request);
        Post newPost = postRequest.toEntity();
        newPost.setUser(userService.findByUsername(jwtTokenUtil.getUsernameFromToken(token)));
        postRepository.save(newPost);
        return newPost;
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
    public Long deletePost(Long postId) {
        postRepository.deleteById(postId);
        return postId;
    }
}
