package pl.edu.ur.pz.clinicapp.views;

import org.junit.jupiter.api.Test;
import pl.edu.ur.pz.clinicapp.models.Doctor;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountDetailsTest {
    @Test
    public void testConvertStringToDefaultVisitDuration() {
        Doctor doctor = new Doctor();
        String durationInput = "60";

        int minutes = Integer.parseInt(durationInput);
        doctor.setDefaultVisitDuration(Duration.ofMinutes(minutes));

        assertEquals(Duration.ofMinutes(60), doctor.getDefaultVisitDuration());
    }

}
