package org.y9nba.app.dto.warning;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class WarningUserDto {
    private final Set<WarningDto> activeWarnings;
    private final Set<WarningDto> otherWarnings;

    public WarningUserDto(Set<WarningDto> activeWarnings, Set<WarningDto> otherWarnings) {
        this.activeWarnings = activeWarnings;
        this.otherWarnings = otherWarnings;
    }
}
