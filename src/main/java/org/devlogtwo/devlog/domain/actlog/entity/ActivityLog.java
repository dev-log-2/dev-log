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
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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
@NamedEntityGraph(
        name = "ActivityLog.withUser",
        attributeNodes = @NamedAttributeNode("user"))
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ActivityType type;

    private Long taskId;

    private Long commentId;

    @Column
    private String methodName;

    @Column(columnDefinition = "TEXT")
    private String parameters;

    @Column(columnDefinition = "TEXT")
    private String result;

    private boolean success;

    @CreatedDate
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Builder(access = AccessLevel.PRIVATE)
    private ActivityLog(User user, ActivityType type, String methodName, String parameters, boolean success,
                        String result, Long taskId, Long commentId) {
        this.user = user;
        this.type = type;
        this.methodName = methodName;
        this.parameters = parameters;
        this.success = success;
        this.result = result;
        this.taskId = taskId;
        this.commentId = commentId;
    }

    public static ActivityLog create(User user, ActivityType type, String methodName, String parameters,
                                     boolean success, String result, Long taskId, Long commentId) {
        return ActivityLog.builder()
                .user(user)
                .type(type)
                .methodName(methodName)
                .parameters(parameters)
                .success(success)
                .result(result)
                .taskId(taskId)
                .commentId(commentId)
                .build();
    }
}
