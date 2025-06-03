package org.y9nba.app.service.impl.token;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.y9nba.app.dao.entity.Session;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.dao.repository.SessionRepository;
import org.y9nba.app.dto.useragent.DeviceInfoDto;
import org.y9nba.app.exception.web.auth.UnAuthorizedException;
import org.y9nba.app.service.face.token.SessionService;
import org.y9nba.app.util.UserAgentUtil;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SessionServiceImpl implements SessionService {

    private final SessionRepository repository;
    private final UserAgentUtil userAgentUtil;

    public SessionServiceImpl(SessionRepository repository, UserAgentUtil userAgentUtil) {
        this.repository = repository;
        this.userAgentUtil = userAgentUtil;
    }

    @Override
    public Session createSession(User user, HttpServletRequest request) {
        Session session = new Session();
        DeviceInfoDto deviceInfoDto = userAgentUtil.parseUserAgent(request.getHeader("User-Agent"));

        session.setLoginTime(LocalDateTime.now());
        session.setLastActive(LocalDateTime.now());
        session.setDeviceType(deviceInfoDto.getDeviceType());
        session.setOperatingSystem(deviceInfoDto.getOperatingSystem());
        session.setBrowser(deviceInfoDto.getBrowser());
        session.setIpAddress(request.getRemoteAddr());
        session.setLoggedOut(false);
        session.setUser(user);

        return repository.save(session);
    }

    @Override
    public void revokeSession(UUID sessionId) {
        repository.findById(sessionId).ifPresent(this::loggedOutSession);

    }

    @Override
    public void revokeSession(Session session) {
        loggedOutSession(session);
    }

    private void loggedOutSession(Session session) {
        session.setLoggedOut(true);
        repository.save(session);
    }

    @Override
    public void revokeAllSessionsExceptCurrent(Long userId, UUID sessionId) {
        Set<Session> sessions = getAllSessionsByUserIdExceptCurrent(userId, sessionId);

        if (!sessions.isEmpty()) {
            sessions.forEach(session ->
                    session.setLoggedOut(true)
            );
        }

        repository.saveAll(sessions);
    }

    @Override
    public void updateLastActive(UUID sessionId) {
        Session session = repository
                .findById(sessionId)
                .orElseThrow(UnAuthorizedException::new);

        session.setLastActive(LocalDateTime.now());
        repository.save(session);
    }

    @Override
    public Long refreshSession(UUID sessionId) {
        Session session = repository
                .findById(sessionId)
                .orElseThrow(UnAuthorizedException::new);

        session.setVersion(session.getVersion() + 1);
        repository.save(session);
        return session.getVersion();
    }

    @Override
    public void revokeAllSessionsByUserId(Long useId) {
        Set<Session> sessions = repository.findAllByUser_Id(useId);

        if (!sessions.isEmpty()) {
            sessions.forEach(session ->
                    session.setLoggedOut(true)
            );
        }

        repository.saveAll(sessions);
    }

    @Override
    public Session getSessionByUserIdAndRequest(Long userId, HttpServletRequest request) {
        DeviceInfoDto deviceInfoDto = userAgentUtil.parseUserAgent(request.getHeader("User-Agent"));

        return repository
                .findByUser_IdAndDeviceTypeAndOperatingSystemAndBrowserAndIpAddressAndLoggedOutIsFalse(
                        userId,
                        deviceInfoDto.getDeviceType(),
                        deviceInfoDto.getOperatingSystem(),
                        deviceInfoDto.getBrowser(),
                        request.getRemoteAddr()
                )
                .orElse(null);
    }

    @Override
    public Session getSessionById(UUID sessionId) {
        Session session = repository
                .findById(sessionId)
                .orElse(null);

        if (session != null && !session.isLoggedOut()) {
            return session;
        }

        return null;
    }

    @Override
    public Set<Session> getAllSessionsByUserIdExceptCurrent(Long userId, UUID sessionId) {
        return repository
                .findAllByUser_Id(userId)
                .stream()
                .filter(s -> !s.getId().equals(sessionId) && !s.isLoggedOut())
                .collect(Collectors.toSet());
    }
}
