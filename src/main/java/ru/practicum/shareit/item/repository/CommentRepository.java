package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> getAllByItemId(Long itemId);

    @Query(value = "SELECT * FROM comments WHERE item_id IN (SELECT item_id FROM items WHERE item_owner = ?1)",
            nativeQuery = true)
    List<Comment> getAllByOwnerId(Long ownerId);
}