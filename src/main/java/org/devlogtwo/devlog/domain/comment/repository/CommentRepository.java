package org.devlogtwo.devlog.domain.comment.repository;

import java.util.List;
import org.devlogtwo.devlog.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByTaskIdAndParentIsNull(Long taskId, Pageable pageable);

    List<Comment> findByParentInOrderByCreatedAtAsc(List<Comment> parents);
}
