package com.human.mini_prj.service;

import com.human.mini_prj.dto.SignUpReqDto;
import com.human.mini_prj.dto.TokenDto;
import com.human.mini_prj.entity.Member;
import com.human.mini_prj.exception.CustomException;
import com.human.mini_prj.repository.MemberRepository;
import com.human.mini_prj.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    // 회원 가입 여부 확인
    public boolean isDuplicatedEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    // 회원 가입
    public boolean signUp(SignUpReqDto dto) {
        try {
            Member member = toEntity(dto);
            memberRepository.save(member);
            return true;
        } catch (Exception e) {
            log.error("회원 가입 시 오류 발생: {}", e.getMessage());
            return false;
        }
    }

    // 로그인: 기존 BCrypt 비밀번호와 과거 평문 비밀번호 모두 처리
    @Transactional
    public TokenDto login(String email, String pwd) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(this::loginFailed);

        String storedPassword = member.getPwd();
        boolean passwordMatches;

        if (isBcryptPassword(storedPassword)) {
            passwordMatches = passwordEncoder.matches(pwd, storedPassword);
        } else {
            // 기존 데이터가 평문으로 저장된 경우에만 일치 여부를 확인하고 즉시 BCrypt로 전환
            passwordMatches = storedPassword != null
                    && MessageDigest.isEqual(
                            pwd.getBytes(StandardCharsets.UTF_8),
                            storedPassword.getBytes(StandardCharsets.UTF_8)
                    );

            if (passwordMatches) {
                member.setPwd(passwordEncoder.encode(pwd));
            }
        }

        if (!passwordMatches) {
            throw loginFailed();
        }

        return tokenProvider.createToken(member.getEmail(), member.getAuthority());
    }

    private boolean isBcryptPassword(String password) {
        return password != null
                && password.length() == 60
                && (password.startsWith("$2a$")
                || password.startsWith("$2b$")
                || password.startsWith("$2y$"));
    }

    private CustomException loginFailed() {
        return new CustomException(
                HttpStatus.UNAUTHORIZED,
                "LOGIN_FAILED",
                "이메일 또는 비밀번호가 올바르지 않습니다."
        );
    }

    // DTO -> Entity
    private Member toEntity(SignUpReqDto dto) {
        Member member = new Member();
        member.setName(dto.getName());
        member.setPwd(passwordEncoder.encode(dto.getPwd()));
        member.setEmail(dto.getEmail());
        return member;
    }
}
