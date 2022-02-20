package walkbook.server.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import walkbook.server.advice.exception.CUserNotFoundException;
import walkbook.server.domain.User;
import walkbook.server.dto.user.UserRequest;
import walkbook.server.dto.user.UserResponse;
import walkbook.server.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public User findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(CUserNotFoundException::new);
        return user;
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(CUserNotFoundException::new);
        return user;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAllUser() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public User update(UserDetails requestUser, Long id, UserRequest userRequest) {
        User user = userRepository
                .findById(id).orElseThrow(CUserNotFoundException::new);
        checkSameUser(requestUser, user.getUsername());
        user.setNickname(userRequest.getNickname());
        user.setGender(userRequest.getGender());
        user.setAge(userRequest.getAge());
        user.setLocation(userRequest.getLocation());
        user.setIntroduction(userRequest.getIntroduction());
        return user;
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private void checkSameUser(UserDetails requestUser, String username) {
        if (!requestUser.getUsername().equals(username)) {
            throw new AccessDeniedException("수정할 권한이 없습니다.");
        }
    }
}
