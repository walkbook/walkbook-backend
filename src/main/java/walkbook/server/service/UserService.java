package walkbook.server.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public Long update(Long id, UserRequest userRequest) {
        User modifiedUser = userRepository
                .findById(id).orElseThrow(CUserNotFoundException::new);
        modifiedUser.setNickname(userRequest.getNickname());
        modifiedUser.setGender(userRequest.getGender());
        modifiedUser.setAge(userRequest.getAge());
        modifiedUser.setLocation(userRequest.getLocation());
        modifiedUser.setIntroduction(userRequest.getIntroduction());
        return id;
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
