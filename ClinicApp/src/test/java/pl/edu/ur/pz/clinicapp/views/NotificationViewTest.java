package pl.edu.ur.pz.clinicapp.views;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.*;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.ur.pz.clinicapp.ClinicApplication;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationViewTest {

    @Test
    public void testWasReadPLWhenIsTrue() {

        NotificationsView notificationsView = new NotificationsView();

        String result = notificationsView.wasReadPL(true);

        assertEquals("Tak", result);
    }

    @Test
    public void testWasReadPLWhenIsFalse() {
        NotificationsView notificationsView = new NotificationsView();

        String result = notificationsView.wasReadPL(false);

        assertEquals("Nie", result);
    }
}
