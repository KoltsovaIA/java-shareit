package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @NotBlank(message = "Новый комментарий не может быть пустым.")
    @Column(name = "comment_text", nullable = false)
    private String text;

    @NotNull(message = "При создании комментария к вещи необходимо указать её id.")
    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @NotNull(message = "При создании комментария к вещи необходимо передать id автора.")
    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}