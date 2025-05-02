package org.y9nba.app.controller.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.y9nba.app.dto.search.UserSearchDto;
import org.y9nba.app.model.UserModel;
import org.y9nba.app.service.impl.UserServiceImpl;

import java.util.Set;

@RestController
@RequestMapping("/user/search")
public class UserSearchController {

    private final UserServiceImpl userService;

    public UserSearchController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping
    public Set<UserSearchDto> getAllUsers(@AuthenticationPrincipal UserModel user) {
        return userService.getAllUsers(user.getId());
    }

    // TODO: добавить поиск users по файлу, чтобы можно было найти пользователей, которые имеют доступ к файлу
}
