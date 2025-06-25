package org.y9nba.app.util;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.y9nba.app.exception.web.auth.oauth2.InvalidOAuth2StateException;

import java.util.UUID;

@Component
public class StateUtil {

    private static final String OAUTH2_STATE = "oauth2_state_";

    public String generateStateWithUserId(HttpSession httpSession, String userId) {
        String state = UUID.randomUUID().toString();
        httpSession.setAttribute(OAUTH2_STATE + state, userId);
        return state;
    }

    public void checkState(HttpSession httpSession, String state) {
        if (httpSession.getAttribute(OAUTH2_STATE + state) == null) {
            throw new InvalidOAuth2StateException();
        } else {
            httpSession.removeAttribute(OAUTH2_STATE + state);
        }
    }
}
