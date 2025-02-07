package org.y9nba.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.y9nba.app.model.UserRoleModel;

import java.util.Set;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleModel, UserRoleModel.UserRoleId> {

    Set<UserRoleModel> getUserRoleModelsByUserId(Long userId);
}
