package org.y9nba.app.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.y9nba.app.service.impl.token.onetime.OneTimeTokenCleanServiceImpl;
import org.y9nba.app.service.impl.token.session.SessionCleanServiceImpl;
import org.y9nba.app.service.impl.user.UserCleanServiceImpl;

@Configuration
@Slf4j
public class ScheduledConfig {

    private final SessionCleanServiceImpl sessionCleanService;
    private final OneTimeTokenCleanServiceImpl oneTimeTokenCleanService;
    private final UserCleanServiceImpl userCleanService;

    public ScheduledConfig(SessionCleanServiceImpl sessionCleanService, OneTimeTokenCleanServiceImpl oneTimeTokenCleanService, UserCleanServiceImpl userCleanService) {
        this.sessionCleanService = sessionCleanService;
        this.oneTimeTokenCleanService = oneTimeTokenCleanService;
        this.userCleanService = userCleanService;
    }

    @Scheduled(cron = "0 0 0 7 * *")
    @Async("scheduledTaskExecutor")
    public void cleanSessions() {
        log.info("Start clean logged out sessions");

        sessionCleanService.deleteAllLogoutSessions();

        log.info("All logged out sessions cleaned");
    }

    @Scheduled(cron = "0 0 0 7 * *")
    @Async("scheduledTaskExecutor")
    public void cleanUsedAndExpiredOneTimeTokens() {
        log.info("Start clean one time tokens");

        oneTimeTokenCleanService.deleteExpiredOneTimeTokens();

        log.info("All used and expired one time tokens cleaned");
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Async("scheduledTaskExecutor")
    public void cleanUnActivatedUsers() {
        log.info("Start clean unactivated users");

        userCleanService.deleteUnactivatedUsers();

        log.info("All unactivated users cleaned");
    }
}
