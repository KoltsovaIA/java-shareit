package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.LinkedList;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    LinkedList<ItemRequest> getAllByRequesterId(Long requesterId);

    LinkedList<ItemRequest> findAllByRequesterIdNot(Long requesterId, Pageable page);
}