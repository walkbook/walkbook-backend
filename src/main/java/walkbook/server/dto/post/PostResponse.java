package walkbook.server.dto.post;

import lombok.Getter;
import walkbook.server.domain.Post;

import java.time.format.DateTimeFormatter;

@Getter
public class PostResponse {
    private final Long postId;
    private final Long userId;
    private final String username;
    private final String title;
    private final String description;
    private final String startLocation;
    private final String finishLocation;
    private final String tmi;
    private final String createdDate;
    private final String modifiedDate;

    public PostResponse(Post post) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.postId = post.getPostId();
        this.userId = post.getUser().getUserId();
        this.username = post.getUser().getUsername();
        this.title = post.getTitle();
        this.description = post.getDescription();
        this.startLocation = post.getStartLocation();
        this.finishLocation = post.getFinishLocation();
        this.tmi = post.getTmi();
        this.createdDate = post.getCreatedDate().format(format);
        this.modifiedDate = post.getModifiedDate().format(format);
    }
}
