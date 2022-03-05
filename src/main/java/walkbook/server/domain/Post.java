package walkbook.server.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import walkbook.server.dto.post.PostRequest;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "posts")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Post {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    private String title;

    private String description;

    private String startLocation;

    private String finishLocation;

    private String tmi;

    private Long likeCount;

    @OneToMany(fetch = LAZY, mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<PostLike> likeList;

    public void mappingPostLike(PostLike postLike) {
        this.likeList.add(postLike);
        updateLikeCount();
    }

    public void removePostLike(PostLike postLike) {
        this.likeList.remove(postLike);
        updateLikeCount();
    }

    public void updateLikeCount() {
        this.likeCount = (long) this.likeList.size();
    }

    public Long commentCount;

    @OneToMany(fetch = LAZY, mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<PostComment> commentList;

    public void mappingPostComment(PostComment postComment) {
        this.commentList.add(postComment);
        updateCommentCount();
    }

    public void removePostComment(PostComment postComment) {
        this.commentList.remove(postComment);
        updateCommentCount();
    }

    public void updateCommentCount() {
        this.commentCount = (long) this.commentList.size();
    }

    @PostPersist
    public void afterInsert(){
        this.likeList = new ArrayList<>();
        this.commentList = new ArrayList<>();
        updateLikeCount();
        updateCommentCount();
    }

    public void set(PostRequest postRequest){
        this.title = postRequest.getTitle();
        this.description = postRequest.getDescription();
        this.startLocation = postRequest.getStartLocation();
        this.finishLocation = postRequest.getFinishLocation();
        this.tmi = postRequest.getTmi();
    }
}
