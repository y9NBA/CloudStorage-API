package org.y9nba.app.dto.warning;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarningActiveCountDto {
    private final int activeCount;
    private final int limitCount;

    public WarningActiveCountDto(int activeCount, int limitCount) {
        this.activeCount = activeCount;
        this.limitCount = limitCount;
    }
}
