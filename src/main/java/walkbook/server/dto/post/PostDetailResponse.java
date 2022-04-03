package walkbook.server.dto.post;

import lombok.Getter;
import lombok.Setter;
import walkbook.server.domain.Post;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PostDetailResponse extends PostResponse{
    private final String startLocation;
    private final String finishLocation;
    private final String tmi;
    private final String createdDate;
    private final String modifiedDate;
    private List<PostCommentResponse> comments;

    public PostDetailResponse(Post post) {
        super(post);
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.startLocation = post.getStartLocation();
        this.finishLocation = post.getFinishLocation();
        this.tmi = post.getTmi();
        this.createdDate = post.getCreatedDate().format(format);
        this.modifiedDate = post.getModifiedDate().format(format);
        this.comments = new ArrayList<>();
    }
}
