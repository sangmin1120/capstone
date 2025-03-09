package smu.capstone.web.jwt.redisrepository;

import org.springframework.data.repository.CrudRepository;
import smu.capstone.web.jwt.redisdomain.RefreshTokenCache;

public interface RefreshTokenCacheRepository extends CrudRepository<RefreshTokenCache, String> {
}
