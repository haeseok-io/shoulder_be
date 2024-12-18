package org.shoulder.backend.security;

import lombok.RequiredArgsConstructor;
import org.shoulder.backend.entity.Member;
import org.shoulder.backend.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username);

        if( member==null ) {
            throw new UsernameNotFoundException("아이디가 존재하지 않습니다.");
        }

        return new CustomUserDetails(member);
    }
}
