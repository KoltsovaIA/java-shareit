package ru.practicum.shareit.reqestTest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.requestDto.IncomingItemRequestDto;
import ru.practicum.shareit.request.dto.OutgoingItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@Slf4j
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestIntegrationTest {
    private final ItemRequestService requestService;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    private static User user;
    private static User user2;
    private static ItemRequest itemRequest;
    private static IncomingItemRequestDto incomingItemRequestDto;

    @BeforeAll
    static void beforeAll() {
        user = User.builder()
                .id(1L)
                .name("name")
                .email("user@mail.ru")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("userName")
                .email("mail2@ya.ru")
                .build();

        incomingItemRequestDto = IncomingItemRequestDto.builder()
                .description("description")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.now())
                .requester(user)
                .build();
    }

    @Test
    void createItemRequestTest() {
        userRepository.save(user);
        requestService.createItemRequest(user.getId(), incomingItemRequestDto);
        TypedQuery<ItemRequest> query = entityManager
                .createQuery("select r from ItemRequest r where r.id = :id", ItemRequest.class);
        ItemRequest requestFromDb = query.setParameter("id", 1L).getSingleResult();
        assertThat(itemRequest.getDescription(), equalTo(requestFromDb.getDescription()));
        Assertions.assertThat(requestFromDb.getRequester())
                .hasFieldOrPropertyWithValue("id", user.getId())
                .hasFieldOrPropertyWithValue("email", user.getEmail())
                .hasFieldOrPropertyWithValue("name", user.getName());
    }

    @Test
    void findItemRequestsByIdTest() {
        userRepository.save(user);
        requestService.createItemRequest(user.getId(), incomingItemRequestDto);
        OutgoingItemRequestDto requestById = requestService.findItemRequestsById(user.getId(), itemRequest.getId());
        assertThat(requestById.getDescription(), equalTo(itemRequest.getDescription()));
    }

    @Test
    void getAllItemRequestTest() {
        userRepository.save(user);
        userRepository.save(user2);
        requestService.createItemRequest(user.getId(), incomingItemRequestDto);
        List<OutgoingItemRequestDto> requests = requestService.getAllItemRequest(user2.getId(), (short) 0, (short) 5);
        Assertions.assertThat(requests)
                .isNotEmpty()
                .hasSize(1)
                .satisfies(list -> Assertions.assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 1L)
                        .hasFieldOrPropertyWithValue("description", itemRequest.getDescription()));
    }
}