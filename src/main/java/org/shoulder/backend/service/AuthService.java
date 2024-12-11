package org.shoulder.backend.service;

import org.shoulder.backend.entity.Member;
import org.shoulder.backend.request.RegisterRequest;

public interface AuthService {
    Member createMember(RegisterRequest request);
    void checkEmailDuplicate(String email); // 이메일 중복 체크
    void checkEmailExists(String email); // 이메일 존재여부 체크
}
