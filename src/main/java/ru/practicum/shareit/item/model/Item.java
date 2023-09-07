package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "items")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @NotBlank(message = "При создании новой вещи необходимо передать её название.")
    @Column(name = "item_name", length = 100, nullable = false)
    private String name;

    @NotBlank(message = "При создании новой вещи необходимо передать её описание.")
    @Column(name = "item_description", length = 200, nullable = false)
    private String description;

    @NotNull(message = "При создании новой вещи необходимо указать статус бронирования.")
    @Column(name = "item_available", nullable = false)
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_owner")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private ItemRequest itemRequest;
}