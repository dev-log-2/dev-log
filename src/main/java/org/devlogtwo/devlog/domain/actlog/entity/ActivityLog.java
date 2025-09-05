package org.devlogtwo.devlog.domain.actlog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.devlogtwo.devlog.common.type.ActivityType;
import org.devlogtwo.devlog.domain.user.entity.User;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private User user;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ActivityType type;

    @NotNull
    private Long taskId;

    private Long commentId;

    @NotNull
    private String description;

    @CreatedDate
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Builder(access = AccessLevel.PRIVATE)
    private ActivityLog(User user, ActivityType type, Long taskId, Long commentId, String description) {
        this.user = user;
        this.taskId = taskId;
        this.commentId = commentId;
        this.type = type;
        this.description = description;
    }

    public static ActivityLog create(User user, ActivityType type, Long taskId, Long commentId, String description) {
        return ActivityLog.builder()
                .user(user)
                .taskId(taskId)
                .commentId(commentId)
                .type(type)
                .description(description)
                .build();
    }
}
