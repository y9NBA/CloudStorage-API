package org.y9nba.app.repository;

import org.springframework.stereotype.Repository;
import org.y9nba.app.base.repository.BaseRepository;
import org.y9nba.app.model.UserModel;

@Repository
public interface UserRepository extends BaseRepository<UserModel, Long> {
}
