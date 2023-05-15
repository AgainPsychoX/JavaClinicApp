package pl.edu.ur.pz.clinicapp.tests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import pl.edu.ur.pz.clinicapp.models.User;


class UserTest {

    @Test
    void getDatabaseUsernameForInput() {
        User user = new User();
        assertEquals("ulj6459", user.getDatabaseUsernameForInput("92010787556"));

    }
}