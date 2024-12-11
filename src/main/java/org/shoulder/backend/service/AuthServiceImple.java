package org.shoulder.backend.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.shoulder.backend.common.CustomException;
import org.shoulder.backend.entity.Member;
import org.shoulder.backend.repository.MemberRepository;
import org.shoulder.backend.request.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImple implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public Member createMember(RegisterRequest request) {
        checkEmailDuplicate(request.getEmail());

        // 회원추가
        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build();
        memberRepository.save(member);

        return member;
    }

    @Override
    public void checkEmailExists(String email) { // 이메일 존재여부 체크
        if( memberRepository.findByEmail(email)==null ) {
            throw new CustomException("EMAIL_NOT_FOUND", "존재하지 않는 이메일 입니다.");
        }
    }

    @Override
    public void checkEmailDuplicate(String email) { // 이메일 중복여부 체크
        if( memberRepository.findByEmail(email)!=null ) {
            throw new CustomException("EMAIL_DUPLICATE", "현재 사용중인 이메일 입니다.");
        }
    }
}
