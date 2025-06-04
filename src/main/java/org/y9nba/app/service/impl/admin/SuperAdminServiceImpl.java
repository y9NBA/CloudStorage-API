package org.y9nba.app.service.impl.admin;

import org.springframework.stereotype.Service;
import org.y9nba.app.constant.Role;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.dao.repository.UserRepository;
import org.y9nba.app.dto.admin.AdminCreateDto;
import org.y9nba.app.dto.user.UserCreateDto;
import org.y9nba.app.service.face.admin.SuperAdminService;
import org.y9nba.app.service.impl.email.AccountInfoServiceImpl;
import org.y9nba.app.service.impl.user.UserSearchServiceImpl;
import org.y9nba.app.util.PasswordUtil;

@Service
public class SuperAdminServiceImpl implements SuperAdminService {

    private final UserRepository repository;
    private final PasswordUtil passwordUtil;

    private final AccountInfoServiceImpl accountInfoService;
    private final UserSearchServiceImpl userSearchService;

    public SuperAdminServiceImpl(UserRepository repository, PasswordUtil passwordUtil, AccountInfoServiceImpl accountInfoService, UserSearchServiceImpl userSearchService) {
        this.repository = repository;
        this.passwordUtil = passwordUtil;
        this.accountInfoService = accountInfoService;
        this.userSearchService = userSearchService;
    }

    @Override
    public User createAdmin(AdminCreateDto dto) {
        String password = dto.getPassword();

        UserCreateDto userCreateDto = new UserCreateDto(
                dto.getUsername(),
                dto.getEmail(),
                passwordUtil.encode(password)
        );

        User model = new User(userCreateDto);

        model.setRole(Role.ROLE_ADMIN);
        model.setEnabled(true);
        model.setStorageLimit(0L);

        accountInfoService.sendAdminAccountInfo(
                model.getEmail(),
                model.getUsername(),
                password
        );

        return repository.save(model);
    }

    @Override
    public void deleteAdminById(Long id) {
        userSearchService.getAdminById(id);
        repository.deleteById(id);
    }
}
