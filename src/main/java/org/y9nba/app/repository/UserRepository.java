package org.y9nba.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.y9nba.app.model.UserModel;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
}
