package smu.capstone.domain.member.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.capstone.domain.member.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByAccountId(String accountId);
    boolean existsByAccountId(String accountId);

    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
