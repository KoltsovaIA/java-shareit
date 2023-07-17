package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "items")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
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

    @Column(name = "item_owner")
    private long owner;
}