package walkbook.server.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import walkbook.server.domain.Post;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {
    private String title;
    private String description;
    private String startLocation;
    private String finishLocation;
    private String tmi;

    public Post toEntity() {
        return Post.builder()
                .title(title)
                .description(description)
                .startLocation(startLocation)
                .finishLocation(finishLocation)
                .tmi(tmi)
                .build();
    }
}
