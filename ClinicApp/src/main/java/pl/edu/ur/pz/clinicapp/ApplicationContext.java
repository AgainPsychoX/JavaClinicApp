package pl.edu.ur.pz.clinicapp;

import pl.edu.ur.pz.clinicapp.models.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.security.auth.login.LoginException;
import java.util.Locale;
import java.util.Properties;

public interface ApplicationContext {
    /**
     * Provides access to application-wide properties/configuration/settings.
     * @return Properties object.
     */
    Properties getProperties();

    default Locale getLocale() {
        return new Locale(getProperties().getProperty("locale"));
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Provides access to the entity manager that allows operating persistable data,
     * connected with login details of current user, or anonymous if no one logged-in.
     * @return Entity manager.
     */
    EntityManager getEntityManager();

    /**
     * Seeds the database for initial usage.
     */
    void seedDatabase() throws PersistenceException;

    /**
     * Connects to database anonymously, in order to fetch user details while logging in or view public data as guest.
     */
    void connectToDatabaseAnonymously() throws PersistenceException;

    /**
     * Connects to database using specified user credentials.
     */
    void connectToDatabaseAsUser(String emailOrPESEL, String password) throws LoginException, PersistenceException;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Provides access to currently logged-in user. Makes sure the instance is managed by the entity manager.
     * @return Currently logged-in user, or null if no one logged-in.
     */
    User getUser();

    /**
     * Provides access to currently logged-in user or throws if no one logged-in.
     * @throws IllegalStateException When no user is logged-in.
     * @return Currently logged-in user
     */
    default User requireUser() throws IllegalStateException {
        final var user = getUser();
        if (user == null) {
            throw new IllegalStateException("");
        }
        return user;
    }
}
