package org.y9nba.app.service.impl;

import org.springframework.stereotype.Service;
import org.y9nba.app.base.service.BaseServiceImpl;
import org.y9nba.app.model.UserModel;
import org.y9nba.app.repository.UserRepository;
import org.y9nba.app.service.UserService;

import java.util.Set;

@Service
public class UserServiceImpl extends BaseServiceImpl<UserRepository, UserModel, Long> implements UserService {

    public UserServiceImpl(UserRepository userRepository) {
        super(userRepository);
    }

}
