package org.y9nba.app.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.y9nba.app.dao.entity.Session;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {

    Optional<Session> findByUser_IdAndDeviceTypeAndOperatingSystemAndBrowserAndIpAddressAndLoggedOutIsFalse(Long user_id, String deviceType, String operatingSystem, String browser, String ipAddress);
    Set<Session> findAllByUser_Id(Long user_id);
    void deleteAllByLoggedOutIsTrue();
}
