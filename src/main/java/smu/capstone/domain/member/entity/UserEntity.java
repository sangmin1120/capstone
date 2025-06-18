package smu.capstone.domain.member.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import smu.capstone.common.domain.BaseEntity;
import smu.capstone.domain.member.dto.AuthRequestDto;


/**
 * 기본적인 회원 정보
 * todo - 회원의 검증 조건 미완성, mysql camel 형식 문제
 */
@Entity()
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users")
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String accountId;
    @Column(unique = true)
    String email;
    String password;
    String username;
    String phoneNumber; // 010-1111-1111: String 형태로 저장
    String birth; // year.birth.day: String 형태로 저장
    String imgUrl;
    String fcmToken; // fcmToken 추가 - 프론트 단에서 가져오기, 하나만 등록 가능
    @Column(nullable = false)
    private boolean isDeleted; // 회원 탈퇴에 T/F

    @Enumerated(EnumType.STRING)
    UserType userType; // enum 으로 변경 ( 환자/간병인 )

    @Enumerated(EnumType.STRING)
    Authority authority;

    public void withdraw() {
        this.isDeleted = true;
        // 탈퇴 시간 설정?
    }
    public void restore() {
        this.isDeleted = false;
    }

    public void changeDate(AuthRequestDto.SignUp dto, PasswordEncoder passwordEncoder) {
        this.accountId = dto.getAccountId();
        this.password = passwordEncoder.encode(dto.getPassword());
        this.username = dto.getUsername();
        this.phoneNumber = dto.getPhoneNumber();
        this.birth = dto.getBirth();
        this.userType = dto.getUserType();
        this.authority = Authority.ROLE_USER;
    }
}
