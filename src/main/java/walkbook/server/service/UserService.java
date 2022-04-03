package walkbook.server.service;

import org.springframework.security.core.userdetails.UserDetails;
import walkbook.server.domain.User;
import walkbook.server.dto.user.UserRequest;

public interface UserService {
    User findById(Long id);
    User findByUsername(String username);
    User update(UserDetails requestUser, Long id, UserRequest userRequest);
}
