package walkbook.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import walkbook.server.domain.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAll(Pageable pageable);

    Page<Post> findByTitleContaining(Pageable pageable, String title);

    Page<Post> findByDescriptionContaining(Pageable pageable, String description);

    Post findAllByPostId(Long postId);
}
