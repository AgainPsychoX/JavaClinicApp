package pl.edu.ur.pz.clinicapp;

import pl.edu.ur.pz.clinicapp.models.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.security.auth.login.LoginException;
import java.util.Locale;
import java.util.Properties;

public class MockApplicationContext implements ApplicationContext {
    public static void use(ApplicationContext context) {
        ClinicApplication.context = context;
    }

    @Override
    public Properties getProperties() {
        return null;
    }

    @Override
    public Locale getLocale() {
        return ApplicationContext.super.getLocale();
    }

    @Override
    public EntityManager getEntityManager() {
        return null;
    }

    @Override
    public void seedDatabase() throws PersistenceException {

    }

    @Override
    public void connectToDatabaseAnonymously() throws PersistenceException {

    }

    @Override
    public void connectToDatabaseAsUser(String emailOrPESEL, String password) throws LoginException, PersistenceException {

    }

    @Override
    public User getUser() {
        return null;
    }
}
