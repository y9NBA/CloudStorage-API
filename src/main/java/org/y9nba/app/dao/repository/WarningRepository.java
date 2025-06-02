package org.y9nba.app.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.y9nba.app.dao.entity.Warning;

import java.util.Set;

@Repository
public interface WarningRepository extends JpaRepository<Warning, Long> {

    Set<Warning> findAllByUser_IdAndActiveTrue(Long id);
    Set<Warning> findAllByUser_Id(Long userId);
}
