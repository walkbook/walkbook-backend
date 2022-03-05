package walkbook.server.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import walkbook.server.domain.Post;
import walkbook.server.domain.User;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @NotNull Page<Post> findAll(@NotNull Pageable pageable);
    Page<Post> findByTitleContainingOrDescriptionContaining(Pageable pageable, String title, String description);
    List<Post> findAllByUser(User user);
}
