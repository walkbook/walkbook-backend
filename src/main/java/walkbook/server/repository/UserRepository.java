package walkbook.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import walkbook.server.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long userId);
    Boolean existsByUsername(String username);
}