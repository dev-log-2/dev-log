package org.devlogtwo.devlog.domain.comment.repository;

import java.util.List;
import org.devlogtwo.devlog.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByTaskIdAndParentIsNull(Long taskId, Pageable pageable);

    List<Comment> findByParentInOrderByCreatedAtAsc(List<Comment> parents);

    @Modifying
    @Query(
            value = "WITH RECURSIVE comment_tree AS (" +
                    "    SELECT id FROM comment WHERE id = :commentId" +
                    "    UNION ALL" +
                    "    SELECT c.id FROM comment c JOIN comment_tree ct ON c.parent_id = ct.id" +
                    ")" +
                    "DELETE FROM comment WHERE id IN (SELECT id FROM comment_tree)",
            nativeQuery = true
    )
    int deleteCommentWithReplies(@Param("commentId") Long commentId);

}
