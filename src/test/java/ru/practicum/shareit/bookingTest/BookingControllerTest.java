package ru.practicum.shareit.bookingTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;
    @MockBean
    private BookingMapper bookingMapper;

    private static ShortBookerDto booker;
    private static IncomingBookingDto incomingBookingDto;
    private static OutgoingBookingDto outgoingBookingDto;
    private static IncomingBookingDto incomingBookingDtoWithoutStart;
    private static IncomingBookingDto incomingBookingDtoWithoutEnd;
    private static Booking booking;
    private static List<Booking> bookingList;
    private static List<OutgoingBookingDto> bookingDtoList;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeAll
    static void beforeAll() {
        LocalDateTime futureOneMonth = LocalDateTime.now().plusMonths(1).withNano(0);
        LocalDateTime futureTwoMonth = LocalDateTime.now().plusDays(2).withNano(0);
        LocalDateTime now = LocalDateTime.now().plusMinutes(3).withNano(0);

        User user = User.builder()
                .id(1L)
                .name("user")
                .email("user@mail.ru")
                .build();

        booker = ShortBookerDto.builder()
                .id(1L)
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(new User(1L, "user1@mail.ru", "name"))
                .itemRequest(null)
                .build();

        ShortItemDto shortitem = ShortItemDto.builder()
                .id(1L)
                .name(item.getName())
                .build();

        incomingBookingDto = IncomingBookingDto.builder()
                .itemId(item.getId())
                .start(now)
                .end(futureOneMonth)
                .build();

        outgoingBookingDto = OutgoingBookingDto.builder()
                .id(1L)
                .item(shortitem)
                .booker(booker)
                .start(now)
                .end(futureTwoMonth)
                .status(BookingStatus.WAITING)
                .build();

        incomingBookingDtoWithoutStart = IncomingBookingDto.builder()
                .itemId(1L)
                .end(futureTwoMonth)
                .build();

        incomingBookingDtoWithoutEnd = IncomingBookingDto.builder()
                .itemId(1L)
                .start(futureOneMonth)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(now)
                .end(futureTwoMonth)
                .item(item)
                .booker(user)
                .approved(BookingStatus.WAITING)
                .build();

        bookingList = new ArrayList<>(List.of(booking));
        bookingDtoList = new ArrayList<>(List.of(outgoingBookingDto));
    }

    @Test
    void createBookingWithoutHeaderTest() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        verify(bookingService, never()).createBooking(anyLong(), any(IncomingBookingDto.class));
    }

    @Test
    void createBookingWithoutStartTest() throws Exception {
        String jsonBooking = objectMapper.writeValueAsString(incomingBookingDtoWithoutStart);
        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, booker.getId())
                        .content(jsonBooking)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).createBooking(anyLong(), any(IncomingBookingDto.class));
    }

    @Test
    void createBookingWithoutEndTest() throws Exception {
        String jsonBooking = objectMapper.writeValueAsString(incomingBookingDtoWithoutEnd);
        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, booker.getId())
                        .content(jsonBooking)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(bookingService, never()).createBooking(anyLong(), any(IncomingBookingDto.class));
    }

    @Test
    void createBookingTest() throws Exception {
        when(bookingService.createBooking(anyLong(), any(IncomingBookingDto.class)))
                .thenReturn(outgoingBookingDto);
        String jsonBooking = objectMapper.writeValueAsString(incomingBookingDto);
        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
                        .content(jsonBooking))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(outgoingBookingDto.getId()))
                .andExpect(jsonPath("$.start").value(outgoingBookingDto.getStart().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.end").value(outgoingBookingDto.getEnd().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.status").value(outgoingBookingDto.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(outgoingBookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(outgoingBookingDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(outgoingBookingDto.getItem().getName()));
        verify(bookingService, times(1)).createBooking(anyLong(), any(IncomingBookingDto.class));
    }

    @Test
    void considerationOfBookingWithoutHeaderTest() throws Exception {
        mockMvc.perform(patch("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        verify(bookingService, never()).considerationOfBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void considerationOfBookingWithWrongApproveTest() throws Exception {
        mockMvc.perform(patch("/bookings/1?approve=text")
                        .header(USER_ID_HEADER, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.ALL_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        verify(bookingService, never()).considerationOfBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void considerationOfBookingTest() throws Exception {
        when(bookingService.considerationOfBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(outgoingBookingDto);
        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header(USER_ID_HEADER, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(outgoingBookingDto.getId()))
                .andExpect(jsonPath("$.start").value(outgoingBookingDto.getStart().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.end").value(outgoingBookingDto.getEnd().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.status").value(outgoingBookingDto.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(outgoingBookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(outgoingBookingDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(outgoingBookingDto.getItem().getName()));
        verify(bookingService, times(1))
                .considerationOfBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void shouldRejectBookingTest() throws Exception {
        when(bookingService.considerationOfBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(outgoingBookingDto);
        mockMvc.perform(patch("/bookings/1?approved=false")
                        .header(USER_ID_HEADER, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(outgoingBookingDto.getId()))
                .andExpect(jsonPath("$.start").value(outgoingBookingDto.getStart().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.end").value(outgoingBookingDto.getEnd().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.status").value(outgoingBookingDto.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(outgoingBookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(outgoingBookingDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(outgoingBookingDto.getItem().getName()));
        verify(bookingService, times(1))
                .considerationOfBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void getAllBookingByOwnerIdTest() throws Exception {
        when(bookingService.getAllBookingByOwnerId(anyLong(), eq("ALL"), any(Short.class), any(Short.class)))
                .thenReturn(bookingDtoList);
        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(1L));
        verify(bookingService, times(1))
                .getAllBookingByOwnerId(anyLong(), eq("ALL"), any(Short.class), any(Short.class));
    }

    @Test
    void getAllBookingByUserIdWithoutHeader() throws Exception {
        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        verify(bookingService, never())
                .getAllBookingByUserId(anyLong(), anyString(), any(Short.class), any(Short.class));
    }

    @Test
    void shouldGetBookingWithGetUserBookings() throws Exception {
        when(bookingService.getAllBookingByUserId(anyLong(), eq("ALL"), any(Short.class), any(Short.class)))
                .thenReturn(bookingDtoList);
        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, booking.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(1L));
        verify(bookingService, times(1))
                .getAllBookingByUserId(anyLong(), eq("ALL"), any(Short.class), any(Short.class));
    }

    @Test
    void getAllBookingByUserIdWithStateALLText() throws Exception {
        when(bookingService.getAllBookingByUserId(anyLong(), eq("ALL"), any(Short.class), any(Short.class)))
                .thenReturn(bookingDtoList);
        mockMvc.perform(get("/bookings?state=ALL")
                        .header(USER_ID_HEADER, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(1L));
        verify(bookingService, times(1))
                .getAllBookingByUserId(anyLong(), eq("ALL"), any(Short.class), any(Short.class));
    }

    @Test
    void getAllBookingByUserIdWithStateCURRENTTest() throws Exception {
        when(bookingService.getAllBookingByUserId(anyLong(), eq("CURRENT"), any(Short.class), any(Short.class)))
                .thenReturn(bookingDtoList);
        mockMvc.perform(get("/bookings?state=CURRENT")
                        .header(USER_ID_HEADER, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(1L));
        verify(bookingService, times(1))
                .getAllBookingByUserId(anyLong(), eq("CURRENT"), any(Short.class), any(Short.class));
    }

    @Test
    void getAllBookingByUserIdWithStatePASTTest() throws Exception {
        when(bookingService.getAllBookingByUserId(anyLong(), eq("PAST"), any(Short.class), any(Short.class)))
                .thenReturn(bookingDtoList);
        mockMvc.perform(get("/bookings?state=PAST")
                        .header(USER_ID_HEADER, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(1L));
        verify(bookingService, times(1))
                .getAllBookingByUserId(anyLong(), eq("PAST"), any(Short.class), any(Short.class));
    }

    @Test
    void getAllBookingByUserIdWithStateFUTURETest() throws Exception {
        when(bookingService.getAllBookingByUserId(anyLong(), eq("FUTURE"), any(Short.class), any(Short.class)))
                .thenReturn(bookingDtoList);
        mockMvc.perform(get("/bookings?state=FUTURE")
                        .header(USER_ID_HEADER, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(1L));
        verify(bookingService, times(1))
                .getAllBookingByUserId(anyLong(), eq("FUTURE"), any(Short.class), any(Short.class));
    }

    @Test
    void getAllBookingByUserIdWithStateWAITINGTest() throws Exception {
        when(bookingService.getAllBookingByUserId(anyLong(), eq("WAITING"), any(Short.class), any(Short.class)))
                .thenReturn(bookingDtoList);
        mockMvc.perform(get("/bookings?state=WAITING")
                        .header(USER_ID_HEADER, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(1L));
        verify(bookingService, times(1))
                .getAllBookingByUserId(anyLong(), eq("WAITING"), any(Short.class), any(Short.class));
    }

    @Test
    void getAllBookingByUserIdWithStateREJECTEDTest() throws Exception {
        when(bookingService.getAllBookingByUserId(anyLong(), eq("REJECTED"), any(Short.class), any(Short.class)))
                .thenReturn(bookingDtoList);
        mockMvc.perform(get("/bookings?state=REJECTED")
                        .header(USER_ID_HEADER, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(1L));
        verify(bookingService, times(1))
                .getAllBookingByUserId(anyLong(), eq("REJECTED"), any(Short.class), any(Short.class));
    }

    @Test
    void getAllBookingByOwnerIdWithoutHeaderTest() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        verify(bookingService, never())
                .getAllBookingByOwnerId(anyLong(), eq("state"), any(Short.class), any(Short.class));
    }

    @Test
    void getAllBookingByOwnerIdBookingsTest() throws Exception {
        when(bookingService.getAllBookingByOwnerId(anyLong(), eq("ALL"), any(Short.class), any(Short.class)))
                .thenReturn(bookingDtoList);
        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(1L));
        verify(bookingService, times(1))
                .getAllBookingByOwnerId(anyLong(), eq("ALL"), any(Short.class), any(Short.class));
    }

    @Test
    void getAllBookingByOwnerIdWithStateALLTest() throws Exception {
        when(bookingService.getAllBookingByOwnerId(anyLong(), eq("ALL"), any(Short.class), any(Short.class)))
                .thenReturn(bookingDtoList);
        mockMvc.perform(get("/bookings/owner?state=ALL")
                        .header(USER_ID_HEADER, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(1L));
        verify(bookingService, times(1))
                .getAllBookingByOwnerId(anyLong(), eq("ALL"), any(Short.class), any(Short.class));
    }

    @Test
    void getAllBookingByOwnerIdWithStateCURRENTTest() throws Exception {
        when(bookingService.getAllBookingByOwnerId(anyLong(), eq("CURRENT"), any(Short.class), any(Short.class)))
                .thenReturn(bookingDtoList);
        mockMvc.perform(get("/bookings/owner?state=CURRENT")
                        .header(USER_ID_HEADER, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(1L));
        verify(bookingService, times(1))
                .getAllBookingByOwnerId(anyLong(), eq("CURRENT"), any(Short.class), any(Short.class));
    }

    @Test
    void getAllBookingByOwnerIdWithStatePASTTest() throws Exception {
        when(bookingService.getAllBookingByOwnerId(anyLong(), eq("PAST"), any(Short.class), any(Short.class)))
                .thenReturn(bookingDtoList);
        mockMvc.perform(get("/bookings/owner?state=PAST")
                        .header(USER_ID_HEADER, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(1L));
        verify(bookingService, times(1))
                .getAllBookingByOwnerId(anyLong(), eq("PAST"), any(Short.class), any(Short.class));
    }

    @Test
    void shouldGetBookingWithGetOwnerBookingsWithStateFUTURE() throws Exception {
        when(bookingService.getAllBookingByOwnerId(anyLong(), eq("FUTURE"), any(Short.class), any(Short.class)))
                .thenReturn(bookingDtoList);
        mockMvc.perform(get("/bookings/owner?state=FUTURE")
                        .header(USER_ID_HEADER, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(1L));
        verify(bookingService, times(1))
                .getAllBookingByOwnerId(anyLong(), eq("FUTURE"), any(Short.class), any(Short.class));
    }

    @Test
    void shouldGetBookingWithGetOwnerBookingsWithStateWAITING() throws Exception {
        when(bookingService.getAllBookingByOwnerId(anyLong(), eq("WAITING"), any(Short.class), any(Short.class)))
                .thenReturn(bookingDtoList);
        mockMvc.perform(get("/bookings/owner?state=WAITING")
                        .header(USER_ID_HEADER, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(1L));
        verify(bookingService, times(1))
                .getAllBookingByOwnerId(anyLong(), eq("WAITING"), any(Short.class), any(Short.class));
    }

    @Test
    void shouldGetBookingWithGetOwnerBookingsWithStateREJECTED() throws Exception {
        when(bookingService.getAllBookingByOwnerId(anyLong(), eq("REJECTED"), any(Short.class), any(Short.class)))
                .thenReturn(bookingDtoList);
        mockMvc.perform(get("/bookings/owner?state=REJECTED")
                        .header(USER_ID_HEADER, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(1L));
        verify(bookingService, times(1))
                .getAllBookingByOwnerId(anyLong(), eq("REJECTED"), any(Short.class), any(Short.class));
    }

    @Test
    void getBookingByUserIdWithoutHeaderTest() throws Exception {
        mockMvc.perform(get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        verify(bookingService, never()).getBookingById(anyLong(), anyLong());
    }

    @Test
    void getBookingByUserIdTest() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(outgoingBookingDto);
        mockMvc.perform(get("/bookings/1")
                        .header(USER_ID_HEADER, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(outgoingBookingDto.getId()))
                .andExpect(jsonPath("$.start").value(outgoingBookingDto.getStart().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.end").value(outgoingBookingDto.getEnd().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.status").value(outgoingBookingDto.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(outgoingBookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(outgoingBookingDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(outgoingBookingDto.getItem().getName()));
        verify(bookingService, times(1)).getBookingById(anyLong(), anyLong());
    }
}