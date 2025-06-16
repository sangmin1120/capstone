package smu.capstone.domain.member.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.capstone.domain.member.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByAccountId(String accountId);
    boolean existsByAccountId(String accountId);
    boolean existsByAccountIdAndIsDeleted(String accountId, boolean isDeleted);

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByEmailAndIsDeleted(String email, boolean isDeleted);
    boolean existsByEmail(String email);
}
