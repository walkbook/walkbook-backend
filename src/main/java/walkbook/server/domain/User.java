package walkbook.server.domain;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "username", length = 20, unique = true)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 6)
    private Gender gender;

    private String age;

    private String location;

    private String introduction;

    private Long postLikeCount;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<PostLike> postLikeList = new ArrayList<>();

    public void mappingPostLike(PostLike postLike) {
        this.postLikeList.add(postLike);
        updateLikeCount();
    }

    public void removePostLike(PostLike postLike) {
        this.postLikeList.remove(postLike);
        updateLikeCount();
    }

    public void updateLikeCount() {
        this.postLikeCount = (long) this.postLikeList.size();
    }
}
