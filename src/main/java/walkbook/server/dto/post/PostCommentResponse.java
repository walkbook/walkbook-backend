package walkbook.server.dto.post;

import lombok.*;
import walkbook.server.domain.PostComment;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentResponse {
    private Long commentId;
    private Long postId;
    private Long authorId;
    private String authorName;
    private String content;
    private String createdDate;
    private String modifiedDate;

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

    public static PostCommentResponse fromEntity(PostComment postComment){
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return PostCommentResponse.builder()
                .commentId(postComment.getCommentId())
                .postId(postComment.getPost().getPostId())
                .authorId(postComment.getUser().getUserId())
                .authorName(postComment.getUser().getNickname())
                .content(postComment.getContent())
                .createdDate(postComment.getCreatedDate().format(format))
                .modifiedDate(postComment.getModifiedDate().format(format))
                .build();
    }
}
