package org.y9nba.app.dto.share;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ExpireRequestDto {
    private int hours;
    private int minutes;

    public int calcExpireTime() {
        return hours * 60 + minutes;
    }
}
