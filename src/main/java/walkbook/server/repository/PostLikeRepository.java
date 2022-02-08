package walkbook.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import walkbook.server.domain.Post;
import walkbook.server.domain.PostLike;
import walkbook.server.domain.User;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostAndUser(Post post, User user);
}
