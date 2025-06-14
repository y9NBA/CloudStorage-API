package org.y9nba.app.exception.local;

import java.io.IOException;

public class IncorrectSuperAdminInitException extends IOException {

    public IncorrectSuperAdminInitException() {
        super("Не удалось создать супер админа, проверьте корректность данных в файле конфигурации");
    }
}
