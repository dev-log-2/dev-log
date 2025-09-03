package org.devlogtwo.devlog.domain.task.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.devlogtwo.devlog.common.entity.BaseTimeEntity;
import org.devlogtwo.devlog.common.type.TaskPriority;
import org.devlogtwo.devlog.common.type.TaskStatus;
import org.devlogtwo.devlog.domain.user.entity.User;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Task extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    private LocalDateTime dueDate;

    private LocalDateTime deletedAt;

    @Builder
    private Task(String title, String description, TaskPriority priority, User assignee, LocalDateTime dueDate) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.TODO;
        this.priority = priority;
        this.assignee = assignee;
        this.dueDate = dueDate;
    }

    // 정적 팩토리 메소드
    public static Task create(String title, String description, TaskPriority priority, User assignee,
                              LocalDateTime dueDate) {
        return Task.builder()
                .title(title)
                .description(description)
                .priority(priority)
                .assignee(assignee)
                .dueDate(dueDate)
                .build();
    }
}
