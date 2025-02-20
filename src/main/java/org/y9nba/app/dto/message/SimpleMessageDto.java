package org.y9nba.app.dto.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleMessageDto<T> {
    private T body;

    public SimpleMessageDto(T body) {
        this.body = body;
    }
}
