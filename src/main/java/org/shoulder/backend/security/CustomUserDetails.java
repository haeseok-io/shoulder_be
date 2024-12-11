package org.shoulder.backend.security;

import org.shoulder.backend.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private final Member member;

    public CustomUserDetails(Member member) {
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return member.getRole().toString();
            }
        });

        return collection;
    }

    @Override
    public boolean isAccountNonExpired() { // 계정이 만료되었는지 확인
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() { // 계정이 잠겨있는지 확인
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() { // 계정의 자격 증명이 만료되었는지 확인
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() { // 계정이 활성화 상태인지 확인
        return UserDetails.super.isEnabled();
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }
}