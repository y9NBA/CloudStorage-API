package org.y9nba.app.dto.share;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SharedUrlResponseDto {
    private final String sharedUrl;
    private final LocalDateTime beginDate;
    private final LocalDateTime endDate;

    public SharedUrlResponseDto(String sharedUrl, int minutes) {
        this.sharedUrl = sharedUrl;
        this.beginDate = LocalDateTime.now();
        this.endDate = beginDate.plusMinutes(minutes);
    }
}
