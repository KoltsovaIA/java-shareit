package ru.practicum.shareit.utilTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.util.OffsetBasedPageRequest;

import static org.junit.jupiter.api.Assertions.*;

class OffsetBasedPageRequestTest {
    @Test
    void getIllegalArgumentExceptionByOffsetTest() {
        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new OffsetBasedPageRequest(-1, 1)
        );
        assertEquals("Offset index must not be less than zero!",
                exception.getMessage());
    }

    @Test
    void getIllegalArgumentExceptionByLimitTest() {
        final IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new OffsetBasedPageRequest(0, 0)
        );
        assertEquals("Limit must not be less than one!",
                exception.getMessage());
    }

    @Test
    void getPageNumberTest() {
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(10, 5);
        assertEquals(2, pageRequest.getPageNumber());
    }

    @Test
    void getNextTest() {
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(10, 5);
        pageRequest.next();
        assertEquals(2, pageRequest.getPageNumber());
    }

    @Test
    void getPreviousTest() {
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(10, 5);
        pageRequest.previous();
        assertEquals(2, pageRequest.getPageNumber());
    }

    @Test
    void getFirstTest() {
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(10, 5);
        pageRequest.first();
        assertEquals(2, pageRequest.getPageNumber());
    }

    @Test
    void equalsTest() {
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(10, 5);
        OffsetBasedPageRequest pageRequest2 = new OffsetBasedPageRequest(10, 5);
        assertEquals(pageRequest2, pageRequest);
    }

    @Test
    void toStringTest() {
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(10, 5);
        assertEquals(pageRequest.toString(), pageRequest.toString());
    }

    @Test
    void hashCodeTest() {
        OffsetBasedPageRequest pageRequest = new OffsetBasedPageRequest(10, 5);
        assertEquals(pageRequest.hashCode(), pageRequest.hashCode());
    }
}