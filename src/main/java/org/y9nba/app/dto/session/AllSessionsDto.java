package org.y9nba.app.dto.session;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class AllSessionsDto {
    private final SessionDto currentSession;
    private final Set<SessionDto> otherSessions;

    public AllSessionsDto(SessionDto currentSession, Set<SessionDto> otherSessions) {
        this.currentSession = currentSession;
        this.otherSessions = otherSessions;
    }
}
