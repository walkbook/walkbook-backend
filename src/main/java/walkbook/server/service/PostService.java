package walkbook.server.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import walkbook.server.domain.Post;
import walkbook.server.dto.post.PostResponse;
import walkbook.server.dto.post.PostRequest;
import walkbook.server.repository.PostRepository;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public Page<PostResponse> getAllPosts(Pageable pageRequest) {
        Page<Post> postList = postRepository.findAll(pageRequest);
        return postList.map(PostResponse::new);
    }

    @Transactional
    public Post savePost(Long userId, PostRequest postRequest) {
        Post newPost = postRequest.toEntity();
        newPost.setUser(userService.findById(userId));
        postRepository.save(newPost);
        return newPost;
    }

    @Transactional(readOnly = true)
    public Post getPostByPostId(Long postId){
        return postRepository.findAllByPostId(postId);
    }

    @Transactional
    public Post editPost(Long postId, PostRequest postRequest){
        Post post = postRepository.findAllByPostId(postId);
        post.setTitle(postRequest.getTitle());
        post.setDescription(postRequest.getDescription());
        post.setStartLocation(postRequest.getStartLocation());
        post.setFinishLocation(postRequest.getFinishLocation());
        post.setTmi(postRequest.getTmi());
        return post;
    }

    @Transactional
    public Long deletePost(Long postId){
        postRepository.deleteById(postId);
        return postId;
    }
}
