package walkbook.server.dto.post;

import lombok.Getter;
import walkbook.server.domain.Post;
import walkbook.server.domain.User;

@Getter
public class PostLikeResponse {
    private final Boolean liked;
    private final Long postLikeCount;

    public PostLikeResponse(Post post, Boolean liked){
        this.liked = liked;
        this.postLikeCount = post.getLikeCount();
    }
}
