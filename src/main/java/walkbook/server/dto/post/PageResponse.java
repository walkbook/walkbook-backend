package walkbook.server.dto.post;

import lombok.Getter;
import walkbook.server.domain.Post;

@Getter
public class PageResponse {
    private final Long postId;
    private final Long authorId;
    private final String authorName;
    private final String title;
    private final String description;

    public PageResponse(Post post) {
        this.postId = post.getPostId();
        this.authorId = post.getUser().getUserId();
        this.authorName = post.getUser().getNickname();
        this.title = post.getTitle();
        this.description = post.getDescription();
    }
}
