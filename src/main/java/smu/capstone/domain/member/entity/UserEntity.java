package smu.capstone.domain.member.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import smu.capstone.domain.member.dto.SignUpDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 기본적인 회원 정보
 * todo - 회원의 검증 조건 미완성, mysql camel 형식 문제
 */
@Entity(name = "users")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String username;

    @Enumerated(EnumType.STRING)
    private UserType userType; // enum 으로 변경 ( 환자/간병인 )

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime createdAt; // 회원가입 날짜 저장

    public static UserEntity toEntity(SignUpDto requestDto) {
        return UserEntity.builder()
                .email(requestDto.getEmail())
                .password(requestDto.getPassword())
                .username(requestDto.getUsername())
                .userType(requestDto.getUserType())
                .role(Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build();
    }
    //다른 정보들 추가
}
