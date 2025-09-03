package org.devlogtwo.devlog.domain.comment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.devlogtwo.devlog.common.entity.BaseTimeEntity;
import org.devlogtwo.devlog.domain.task.entity.Task;
import org.devlogtwo.devlog.domain.user.entity.User;


@Entity
@NoArgsConstructor
@Getter
public class Comment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="userId",nullable = false)
    private User user; //작성자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="taskId",nullable = false)
    private Task task; // 작업

    @Column(nullable = false, length = 1000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId")
    private Comment parent;

    @Builder
    public Comment(User user, Task task, String content, Comment parent) {
        this.user = user;
        this.task = task;
        this.content = content;
        this.parent = parent;
    }
    //정적 팩토리 메소드
    public static Comment create(User user, Task task, String content, Comment parent) {
        return Comment.builder()
                .user(user)
                .task(task)
                .content(content)
                .parent(parent)
                .build();
    }
}
