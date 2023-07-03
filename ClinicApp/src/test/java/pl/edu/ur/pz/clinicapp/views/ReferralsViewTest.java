package pl.edu.ur.pz.clinicapp.views;

import javafx.scene.control.ComboBox;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.models.User;

import static org.junit.jupiter.api.Assertions.*;

class ReferralsViewTest {

    @ParameterizedTest
    @EnumSource(User.Role.class)
    void testCurrQuery(User.Role role) {
        ReferralsView refView = new ReferralsView();
        refView.setCurrQuery(role);
        if(role == User.Role.NURSE) assertEquals(refView.currQuery, refView.nursesReferrals);
        else if(role == User.Role.PATIENT) assertEquals(refView.currQuery, refView.findUsersReferrals);
        else if(role == User.Role.DOCTOR) assertEquals(refView.currQuery, refView.createdReferrals);
        else assertEquals(refView.currQuery, refView.allReferrals);
    }
}