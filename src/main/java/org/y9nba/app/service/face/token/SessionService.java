package org.y9nba.app.service.face.token;

import jakarta.servlet.http.HttpServletRequest;
import org.y9nba.app.dao.entity.Session;
import org.y9nba.app.dao.entity.User;

import java.util.Set;
import java.util.UUID;

public interface SessionService {
    Session createSession(User user, HttpServletRequest request);
    void updateLastActive(UUID sessionId);
    void revokeAllSessionsByUserId(Long useId);
    void revokeSession(UUID sessionId);
    void revokeSession(Session session);
    void revokeAllSessionsExceptCurrent(Long userId, UUID sessionId);
    void deleteAllLogoutSessions();
    Session getSessionByUserIdAndRequest(Long userId, HttpServletRequest request);
    Session getSessionById(UUID sessionId);
    Set<Session> getAllSessionsByUserIdExceptCurrent(Long userId, UUID currentSessionId);
}
