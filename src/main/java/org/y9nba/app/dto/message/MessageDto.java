package org.y9nba.app.dto.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDto<T> extends SimpleMessageDto<T> {

    private String title;

    public MessageDto() {
        super();
    }

    public MessageDto(T body) {
        super(body);
    }

    public MessageDto(String title, T body) {
        super(body);
        this.title = title;
    }
}
