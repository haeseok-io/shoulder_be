package org.shoulder.backend.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.shoulder.backend.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;

        // 로그인 경로 설정
        setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String email = request.getParameter("email");
        String password = obtainPassword(request);

        if( email==null || email.isEmpty() ) {
            sendError(response, "email", "이메일이 존재하지 않습니다.");
            return null;
        }
        if( password==null || password.isEmpty() ) {
            sendError(response, "password", "비밀번호가 존재하지 않습니다.");
            return null;
        }

        // 시큐리티에서 username, password 를 검증하기 위해서 token 에 담아야함
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);

        // 토큰 검증을 위하여 전달
        return authenticationManager.authenticate(authToken);
    }

    // 로그인 성공시 실행되는 메소드 ( 여기서 JWT 발급 )
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();

        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String email = customUserDetails.getUsername();
        String role = auth.getAuthority();

        String token = jwtUtil.createJwt(email, role, 60*60*1000L);

        response.addHeader("Authorization", "Bearer "+token);
    }

    // 로그인 실패시 실행되는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        // 로그인 실패시 401 응답
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }

    private void sendError(HttpServletResponse response, String target, String message) {
        try {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);  // 400 Bad Request
            response.setContentType("application/json");

            // JSON 형식으로 오류 메시지 전송
            String errorMessage = String.format("{\"target\": \"%s\", \"message\": \"%s\"}", target, message);
            response.getWriter().write(errorMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
