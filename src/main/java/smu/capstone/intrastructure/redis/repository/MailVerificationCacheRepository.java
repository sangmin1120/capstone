package smu.capstone.intrastructure.redis.repository;

import org.springframework.data.repository.CrudRepository;
import smu.capstone.intrastructure.redis.domain.MailVerificationCache;

public interface MailVerificationCacheRepository extends CrudRepository<MailVerificationCache, String> {
}
