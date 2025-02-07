package org.y9nba.app.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.y9nba.app.model.UserModel;
import org.y9nba.app.model.UserRoleModel;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtTokenFactory {
    public static JwtEntity create(UserModel user) {
        return new JwtEntity(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                setRoleToAuthorities(user.getUserRoles())
        );
    }

    private static List<GrantedAuthority> setRoleToAuthorities(Set<UserRoleModel> userRoles) {
        return userRoles
                .stream()
                .map(UserRoleModel::getId)
                .map(UserRoleModel.UserRoleId::getRole)
                .map(Enum::name)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
