package smu.capstone.web.jwt.redisrepository;

import org.springframework.data.repository.CrudRepository;
import smu.capstone.web.jwt.redisdomain.MailVerificationCache;

public interface MailVerificationCacheRepository extends CrudRepository<MailVerificationCache, String> {
}
