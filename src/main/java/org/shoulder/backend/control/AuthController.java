package org.shoulder.backend.control;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.shoulder.backend.entity.Member;
import org.shoulder.backend.request.RegisterRequest;
import org.shoulder.backend.response.MemberResponse;
import org.shoulder.backend.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping(value = "/register")
    public ResponseEntity register(@Valid RegisterRequest request, BindingResult result) {
        // 유효성 검사
        if( result.hasErrors() ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getFieldErrors());
        }

        // 회원 생성
        Member member = authService.createMember(request);
        MemberResponse memberResponse = MemberResponse.builder()
                .no(member.getNo())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(memberResponse);
    }

    @GetMapping(value = "/email/duplicate")
    public ResponseEntity emailDuplicate(String email) {
        authService.checkEmailDuplicate(email);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
