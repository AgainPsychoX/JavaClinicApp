package pl.edu.ur.pz.clinicapp.models;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationTest {

    @Test
    public void testWasReadWhenReadDateIsNotNull() {

        Notification notification = new Notification();
        notification.setReadDate(Instant.now()); // Przykładowa data

        boolean result = notification.wasRead();

        assertTrue(result);
    }

    @Test
    public void testWasReadWhenReadDateIsNull() {

        Notification notification = new Notification();

        boolean result = notification.wasRead();

        assertFalse(result);
    }
}