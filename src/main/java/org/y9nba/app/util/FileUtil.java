package org.y9nba.app.util;

import org.springframework.stereotype.Component;

@Component
public class FileUtil {

    public String parsePath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return "";
        }

        return path.trim().replaceAll("^/*|/+$", "/");
    }
}
