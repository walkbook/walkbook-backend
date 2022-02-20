package walkbook.server.dto.post;

import lombok.Getter;
import lombok.Setter;
import walkbook.server.domain.Post;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PostResponse {
    private final Long postId;
    private final Long authorId;
    private final String authorName;
    private final String title;
    private final String description;
    private final String startLocation;
    private final String finishLocation;
    private final String tmi;
    private final String createdDate;
    private final String modifiedDate;
    private Boolean liked;
    private final Long likeCount;
    private final Long commentCount;
    private List comments;

    public PostResponse(Post post) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.postId = post.getPostId();
        this.authorId = post.getUser().getUserId();
        this.authorName = post.getUser().getNickname();
        this.title = post.getTitle();
        this.description = post.getDescription();
        this.startLocation = post.getStartLocation();
        this.finishLocation = post.getFinishLocation();
        this.tmi = post.getTmi();
        this.createdDate = post.getCreatedDate().format(format);
        this.modifiedDate = post.getModifiedDate().format(format);
        this.liked = false;
        this.likeCount = post.getLikeCount();
        this.commentCount = post.getCommentCount();
        this.comments = new ArrayList();
    }
}
