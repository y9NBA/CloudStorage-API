package org.y9nba.app.dto.warning;

import lombok.Getter;
import lombok.Setter;
import org.y9nba.app.constant.Reason;
import org.y9nba.app.dao.entity.Warning;

import java.time.LocalDateTime;

@Getter
@Setter
public class WarningDto {
    private final Long id;
    private final Long userId;
    private final Long adminId;
    private final Reason reason;
    private final LocalDateTime createdAt;
    private final Boolean active;

    public WarningDto(Warning model) {
        this.id = model.getId();
        this.userId = model.getUser().getId();
        this.adminId = model.getAdmin().getId();
        this.reason = model.getReason();
        this.createdAt = model.getCreatedAt();
        this.active = model.getActive();
    }
}
