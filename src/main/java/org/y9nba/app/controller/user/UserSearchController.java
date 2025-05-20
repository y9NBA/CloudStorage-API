package org.y9nba.app.controller.user;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.y9nba.app.dto.search.UserSearchDto;
import org.y9nba.app.model.UserModel;
import org.y9nba.app.service.impl.UserServiceImpl;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/user/search")
public class UserSearchController {

    private final UserServiceImpl userService;

    public UserSearchController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping
    public Set<UserSearchDto> getAllUsers(@RequestParam(required = false) UUID bucketName, @RequestParam(required = false) String email, @RequestParam(required = false) String username, @AuthenticationPrincipal UserModel user) {
        return userService.getAllUsers(username, email, bucketName, user.getId());
    }

    @GetMapping("/{id}")
    public Object getUserById(@PathVariable Long id, @AuthenticationPrincipal UserModel user) {
        if (user.getId().equals(id)) {
            return "redirect:/user/info/profile";
        } else {
            return new UserSearchDto(userService.getById(id));
        }

    }
}
