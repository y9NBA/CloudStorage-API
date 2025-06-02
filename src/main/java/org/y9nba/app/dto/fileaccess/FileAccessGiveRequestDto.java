package org.y9nba.app.dto.fileaccess;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.y9nba.app.constant.Access;
import org.y9nba.app.exception.web.file.access.IllegalAccessLevelException;

@AllArgsConstructor
@Getter
public class FileAccessGiveRequestDto {

    @Schema(description = "ID пользователя, которому будет предоставлен доступ", example = "1")
    private Long collaboratorId;

    @Schema(description = "Уровень доступа (0 - только чтение, 1 - чтение и запись)", example = "0")
    private int accessLevelCode;

    public Access extractAccessLevel() {
        if (accessLevelCode == 0) {
            return Access.ACCESS_READER;
        } else if (accessLevelCode == 1) {
            return Access.ACCESS_EDITOR;
        } else {
            throw new IllegalAccessLevelException();
        }
    }
}
