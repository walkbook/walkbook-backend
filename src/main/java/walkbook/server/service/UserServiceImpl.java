package walkbook.server.service;

import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import walkbook.server.advice.exception.CUserNotFoundException;
import walkbook.server.domain.User;
import walkbook.server.dto.user.UserRequest;
import walkbook.server.repository.UserRepository;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(CUserNotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(CUserNotFoundException::new);
    }

    @Override
    @Transactional
    public User update(UserDetails requestUser, Long id, UserRequest userRequest) {
        User user = userRepository
                .findById(id).orElseThrow(CUserNotFoundException::new);
        checkSameUser(requestUser, user.getUsername());
        user.setNickname(userRequest.getNickname());
        user.setLocation(userRequest.getLocation());
        user.setIntroduction(userRequest.getIntroduction());
        return user;
    }

    private void checkSameUser(UserDetails requestUser, String username) {
        if (!requestUser.getUsername().equals(username)) {
            throw new AccessDeniedException("수정할 권한이 없습니다.");
        }
    }
}
