package org.y9nba.app.service.impl.token.session;

import org.springframework.stereotype.Service;
import org.y9nba.app.dao.repository.SessionRepository;
import org.y9nba.app.service.face.token.session.SessionCleanService;

@Service
public class SessionCleanServiceImpl implements SessionCleanService {

    private final SessionRepository sessionRepository;

    public SessionCleanServiceImpl(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void deleteAllLogoutSessions() {
        sessionRepository.deleteAllByLoggedOutIsTrue();
    }
}
