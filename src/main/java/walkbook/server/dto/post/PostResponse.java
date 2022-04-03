package walkbook.server.dto.post;

import lombok.Getter;
import lombok.Setter;
import walkbook.server.domain.Post;

@Getter
@Setter
public class PostResponse {
    private final Long postId;
    private final Long authorId;
    private final String authorName;
    private final String title;
    private final String description;
    private Boolean liked;
    private final Long likeCount;
    private final Long commentCount;

    public PostResponse(Post post) {
        this.postId = post.getPostId();
        this.authorId = post.getUser().getUserId();
        this.authorName = post.getUser().getNickname();
        this.title = post.getTitle();
        this.description = post.getDescription();
        this.liked = false;
        this.likeCount = post.getLikeCount();
        this.commentCount = post.getCommentCount();
    }
}
