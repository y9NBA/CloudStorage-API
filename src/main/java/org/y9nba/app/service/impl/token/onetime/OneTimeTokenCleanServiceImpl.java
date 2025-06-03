package org.y9nba.app.service.impl.token.onetime;

import org.springframework.stereotype.Service;
import org.y9nba.app.constant.OneTimeTokenType;
import org.y9nba.app.dao.repository.OneTimeTokenRepository;
import org.y9nba.app.service.face.token.onetime.OneTimeTokenCleanService;

@Service
public class OneTimeTokenCleanServiceImpl implements OneTimeTokenCleanService {

    private final OneTimeTokenRepository oneTimeTokenRepository;

    public OneTimeTokenCleanServiceImpl(OneTimeTokenRepository oneTimeTokenRepository) {
        this.oneTimeTokenRepository = oneTimeTokenRepository;
    }

    @Override
    public void deleteExpiredOneTimeTokens() {
        oneTimeTokenRepository
                .findAll()
                .stream()
                .filter(t ->
                        (
                                t.isExpired() || t.isUsed()
                        )
                                && !t.getType().equals(OneTimeTokenType.ACTIVATION)
                ).forEach(oneTimeTokenRepository::delete);
    }
}
