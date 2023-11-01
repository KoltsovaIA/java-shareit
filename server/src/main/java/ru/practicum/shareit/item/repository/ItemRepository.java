package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> getAllByOwnerId(Long ownerId);
    List<Item> getAllByOwnerIdOrderByIdAsc(Long ownerId);

    List<Item> getAllByItemRequestId(Long requestId);

    List<Item> findByNameOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(String stringOne, String stringTwo);
}