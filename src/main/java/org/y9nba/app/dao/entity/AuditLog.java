package org.y9nba.app.dao.entity;

import jakarta.persistence.*;
import lombok.*;
import org.y9nba.app.constant.Action;
import org.y9nba.app.dto.auditlog.AuditLogCreateDto;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Action action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private File file;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public AuditLog(AuditLogCreateDto dto) {
        this.user = dto.getUser();
        this.file = dto.getFile();
        this.action = dto.getAction();
    }
}

