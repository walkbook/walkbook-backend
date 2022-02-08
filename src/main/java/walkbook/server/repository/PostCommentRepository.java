package walkbook.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import walkbook.server.domain.PostComment;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
}
