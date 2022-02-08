package walkbook.server.dto.post;

import lombok.Getter;
import walkbook.server.domain.PostComment;

import java.time.format.DateTimeFormatter;

@Getter
public class PostCommentResponse {
    private final Long commentId;
    private final Long postId;
    private final Long authorId;
    private final String authorName;
    private final String content;
    private final String createdDate;
    private final String modifiedDate;

    public PostCommentResponse(PostComment postComment){
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.commentId = postComment.getCommentId();
        this.postId = postComment.getPost().getPostId();
        this.authorId = postComment.getUser().getUserId();
        this.authorName = postComment.getUser().getNickname();
        this.content = postComment.getContent();
        this.createdDate = postComment.getCreatedDate().format(format);
        this.modifiedDate = postComment.getModifiedDate().format(format);
    }
}
