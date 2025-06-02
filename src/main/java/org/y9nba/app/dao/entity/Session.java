package org.y9nba.app.dao.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sessions")
@Getter
@Setter
@NoArgsConstructor
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "device_type")
    private String deviceType;

    @Column(name = "operating_system")
    private String operatingSystem;

    @Column(name = "browser")
    private String browser;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "login_time")
    private LocalDateTime loginTime;

    @Column(name = "last_active")
    private LocalDateTime lastActive;

    @Column(name = "is_logged_out")
    private boolean loggedOut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}