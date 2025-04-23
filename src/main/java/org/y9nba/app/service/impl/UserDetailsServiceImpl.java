package org.y9nba.app.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.y9nba.app.exception.NotFoundEntryException;
import org.y9nba.app.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;

    public UserDetailsServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository
                .findByUsername(username)
                .orElseThrow(
                        () -> new NotFoundEntryException("Not found user by username: " + username)
                );
    }

    public UserDetails loadUserByUserId(Long id) {
        return repository
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundEntryException("Not found user by id: " + id)
                );
    }
}
