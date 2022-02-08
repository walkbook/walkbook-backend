package walkbook.server.dto.post;

import lombok.Getter;
import walkbook.server.domain.Post;
import walkbook.server.domain.User;

@Getter
public class PostLikeResponse {
    private final Long postId;
    private final Long userId;
    private final Boolean liked;
    private final Long postLikeCount;

    public PostLikeResponse(Post post, User user, Boolean liked){
        this.postId = post.getPostId();
        this.userId = user.getUserId();
        this.liked = liked;
        this.postLikeCount = post.getLikeCount();
    }
}
