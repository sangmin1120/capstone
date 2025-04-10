package smu.capstone.intrastructure.redis.repository;

import org.springframework.data.repository.CrudRepository;
import smu.capstone.intrastructure.redis.domain.RefreshTokenCache;

public interface RefreshTokenCacheRepository extends CrudRepository<RefreshTokenCache, String> {
}
