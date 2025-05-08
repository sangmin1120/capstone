package smu.capstone.domain.member.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import smu.capstone.common.domain.BaseEntity;


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

    @Enumerated(EnumType.STRING)
    UserType userType; // enum 으로 변경 ( 환자/간병인 )

    @Enumerated(EnumType.STRING)
    Authority authority;

    //다른 정보들 추가
    String fcmToken;
}
