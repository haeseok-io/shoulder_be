package org.shoulder.backend.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.shoulder.backend.common.Role;
import org.shoulder.backend.entity.Member;
import org.shoulder.backend.security.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // request 에서 Authorization 헤더를 찾음
        String authorization = request.getHeader("Authorization");

        System.out.println(authorization);

        // Authorization 검증
        if( authorization==null || !authorization.startsWith("Bearer ") ) {
            System.out.println("토큰이 존재하지 않습니다.");
            filterChain.doFilter(request, response);

            return; // 여기서 리턴만해주면되는지 http status 반환하지 않아도 되는지?..
        }

        // Bearer 제거 후 토큰만 획득
        String token = authorization.split(" ")[1];

        // 토큰 소멸시간 검증
        if(jwtUtil.isExpired(token) ) {
            System.out.println("토큰의 유효기간이 만료되었습니다.");
            filterChain.doFilter(request, response);

            return;
        }


        // 토큰에서 회원정보 추출
        String email = jwtUtil.getEmail(token);
        String role = jwtUtil.getRole(token);

        System.out.println(role);

        Member member = Member.builder()
                .email(email)
                .password("temppassword")
                .role(Role.valueOf(role))
                .build();

        System.out.println(member);

        // UserDetails 에 회원 정보 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(member);

        // 스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        // 세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
