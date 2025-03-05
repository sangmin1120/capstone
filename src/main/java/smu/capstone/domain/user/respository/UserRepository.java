package smu.capstone.domain.user.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.capstone.domain.user.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
