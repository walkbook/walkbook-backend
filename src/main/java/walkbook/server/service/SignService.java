package walkbook.server.service;

import walkbook.server.dto.user.UserRequest;

public interface SignService {
    String signin(UserRequest userRequest);
    Long signup(UserRequest userRequest);
}
