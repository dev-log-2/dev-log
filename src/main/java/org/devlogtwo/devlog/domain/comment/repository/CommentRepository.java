package org.devlogtwo.devlog.domain.comment.repository;

import org.devlogtwo.devlog.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
