package org.shoulder.backend.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.shoulder.backend.jwt.JWTFilter;
import org.shoulder.backend.jwt.JWTUtil;
import org.shoulder.backend.jwt.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final JWTFilter jwtFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // cors 설정
        http
                .cors(cors -> cors
                        .configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration corsConfiguration = new CorsConfiguration();

                                corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                                corsConfiguration.setAllowedMethods(Collections.singletonList("*")); // 모든 요청 허용 (GET, POST 등등)
                                corsConfiguration.setAllowCredentials(true);
                                corsConfiguration.setMaxAge(3600L);

                                corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
                                corsConfiguration.setAllowedHeaders(Collections.singletonList("Authorization"));

                                return corsConfiguration;
                            }
                        }));

        // csrf disable
        http.csrf(AbstractHttpConfigurer::disable);
        // form 로그인 방식 disable
        http.formLogin(AbstractHttpConfigurer::disable);
        // http basic 인증 방식 disable
        http.httpBasic(AbstractHttpConfigurer::disable);

        // 인가 작업
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/user/**").hasAuthority("USER")
                        .anyRequest().authenticated());

        // 필터 추가
        // addFilterAt : 해당필터 자리에 대체 등록
        // addFilterBefore : 해당 필터 전에 등록
        // addFilterAfter: 해당 필터 후에 등록

        // LoginFilter 앞에 JWTFilter 등록
        http.addFilterBefore(jwtFilter, LoginFilter.class);

        // LoginFilter 를 UsernamePasswordAuthenticationFilter 자리에 대체하여 등록
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);


        // 세션 설정
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        return http.build();
    }
}
