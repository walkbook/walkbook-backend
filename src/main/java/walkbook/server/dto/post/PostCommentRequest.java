package walkbook.server.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import walkbook.server.domain.PostComment;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentRequest {
    private String content;
    public PostComment toEntity() {
        return PostComment.builder()
                .content(content)
                .build();
    }
}
