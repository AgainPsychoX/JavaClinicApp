package pl.edu.ur.pz.clinicapp.dialogs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import pl.edu.ur.pz.clinicapp.MockApplicationContext;

import javax.persistence.PersistenceException;
import javax.security.auth.login.LoginException;
import java.util.concurrent.TimeoutException;

@ExtendWith(ApplicationExtension.class)
class LoginDialogTest {
    @Test
    void invalidLogin(FxRobot robot) throws TimeoutException {
        MockApplicationContext.use(new MockApplicationContext() {
            @Override
            public void connectToDatabaseAsUser(String emailOrPESEL, String password) throws LoginException, PersistenceException {
                throw new LoginException("Nieprawidłowe dane logowania!");
            }
        });

        FxToolkit.registerStage(LoginDialog::new);
        FxToolkit.showStage();
        robot.interact(() -> robot.clickOn("#identityTextField"));
        robot.interact(() -> robot.write("doctor@example.com"));
        robot.interact(() -> robot.clickOn("#passwordField"));
        robot.interact(() -> robot.write("#invalid password"));
        robot.interact(() -> robot.clickOn("#loginButton"));

        Assertions.assertTrue(robot.lookup("#errorText").queryText().isVisible(), "error text visible");
    }
}