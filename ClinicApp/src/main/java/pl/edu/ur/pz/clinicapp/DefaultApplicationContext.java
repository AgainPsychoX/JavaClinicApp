package pl.edu.ur.pz.clinicapp;

import javafx.application.Platform;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.service.spi.ServiceException;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.utils.ExampleDataSeeder;
import pl.edu.ur.pz.clinicapp.utils.JPAUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.security.auth.login.LoginException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Logger;

import static java.util.regex.Matcher.quoteReplacement;
import static pl.edu.ur.pz.clinicapp.utils.OtherUtils.isStringNullOrBlank;

public class DefaultApplicationContext implements ApplicationContext {
    private static final Logger logger = Logger.getLogger(DefaultApplicationContext.class.getName());

    public DefaultApplicationContext() {
        loadAppProperties();
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    Properties properties;

    @Override
    public Properties getProperties() {
        return properties;
    }

    private void loadAppProperties() {
        try {
            try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("app.default.properties")) {
                final var defaults = new Properties();
                defaults.load(inputStream);
                properties = new Properties(defaults);
            }
            try (FileInputStream appPropertiesFile = new FileInputStream("app.properties")) {
                properties.load(appPropertiesFile);
                logger.finest("Loaded from 'app.properties'");
            }
            catch (FileNotFoundException e) {
                // Ignore, defaults will be used.
                logger.finest("Using default properties");
            }
        }
        catch (IOException e) {
            logger.warning("Error loading properties: " + e.getMessage());
            Platform.exit();
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    EntityManagerFactory entityManagerFactory;
    EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    void disconnectFromDatabase() {
        // TODO: gracefully disconnect from the database (if connected)
        if (getEntityManager() != null) {
            getEntityManager().close();
        }
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }

    @Override
    public void seedDatabase() throws PersistenceException {
        // TODO: allow migration only (minimalist/structure only seeding - no example data)
        disconnectFromDatabase();
        logger.fine("----------------------------------------------------------------");
        logger.info("Seeding database...");
        try {
            if (Boolean.parseBoolean(properties.getProperty("seeding.force-drop"))) {
                entityManagerFactory = Persistence.createEntityManagerFactory("default", Map.ofEntries(
                        Map.entry("hibernate.connection.url", properties.getProperty("database.url")),
                        Map.entry("hibernate.connection.username", properties.getProperty("seeding.username")),
                        Map.entry("hibernate.connection.password", properties.getProperty("seeding.password"))
                ));
                entityManager = entityManagerFactory.createEntityManager();

                logger.finer("Dropping schema");
                JPAUtils.transaction(em -> {
                    entityManager.createNativeQuery("DROP SCHEMA IF EXISTS public CASCADE").executeUpdate();
                    entityManager.createNativeQuery("CREATE SCHEMA public").executeUpdate();
                });

                final var dropUsersPrefix = properties.getProperty("seeding.drop-users");
                if (dropUsersPrefix != null) {
                    logger.finer("Dropping users");
                    final var sql = """
                            DO $$
                            DECLARE
                                role_name TEXT;
                            BEGIN
                                FOR role_name IN (SELECT rolname FROM pg_catalog.pg_roles WHERE rolname LIKE '%s%%' ESCAPE '\\')
                                LOOP
                                    EXECUTE 'DROP OWNED BY ' || quote_ident(role_name);
                            		EXECUTE 'DROP ROLE ' || quote_ident(role_name);
                                END LOOP;
                            END $$
                            """.formatted(dropUsersPrefix.replaceAll("_", quoteReplacement("\\_")));
                    JPAUtils.transaction(em -> em.createNativeQuery(sql).executeUpdate());
                }

                entityManager.close();
                entityManagerFactory.close();
            }

            // For seeding we need superuser and special setting, so we shadow default settings from `persistence.xml`.
            entityManagerFactory = Persistence.createEntityManagerFactory("default", Map.ofEntries(
                    Map.entry("hibernate.connection.url", properties.getProperty("database.url")),
                    Map.entry("hibernate.connection.username", properties.getProperty("seeding.username")),
                    Map.entry("hibernate.connection.password", properties.getProperty("seeding.password")),
                    Map.entry("hibernate.hbm2ddl.auto", "create")
            ));
            entityManager = entityManagerFactory.createEntityManager();

            // Reconnect after running HBM2DDL & SQLs; required as Hibernate is blind after permissions changes
            logger.finer("Reconnecting (planned)");
            entityManager.close();
            entityManagerFactory.close();
            entityManagerFactory = Persistence.createEntityManagerFactory("default", Map.ofEntries(
                    Map.entry("hibernate.connection.url", properties.getProperty("database.url")),
                    Map.entry("hibernate.connection.username", properties.getProperty("seeding.username")),
                    Map.entry("hibernate.connection.password", properties.getProperty("seeding.password"))
            ));
            entityManager = entityManagerFactory.createEntityManager();

            // Invoke example data seeder
            long seed = new Random().nextLong();
            final var seedString = properties.getProperty("seeding.seed");
            if (!isStringNullOrBlank(seedString)) {
                seed = Long.parseLong(seedString);
            }
            new ExampleDataSeeder(entityManager, seed, getLocale()).run();
            // TODO: make example data seeding optional (minimalist/structure only mode)

            logger.info("Finished seeding!");
        }
        finally {
            logger.fine("----------------------------------------------------------------");
        }
    }

    @Override
    public void connectToDatabaseAnonymously() throws PersistenceException {
        disconnectFromDatabase();

        // For anonymous connect, the login details are used from  `persistence.xml` along other settings.
        entityManagerFactory = Persistence.createEntityManagerFactory("default", Map.ofEntries(
                Map.entry("hibernate.connection.url", properties.getProperty("database.url"))
        ));
        entityManager = entityManagerFactory.createEntityManager();

        user = null;
    }

    @Override
    public void connectToDatabaseAsUser(String emailOrPESEL, String password) throws LoginException, PersistenceException {
        try {
            final var username = User.getDatabaseUsernameForInput(emailOrPESEL);
            logger.fine("Database username for '%s' is '%s'".formatted(emailOrPESEL, username));

            // We shadow default (anonymous) login details from `persistence.xml` with user specific ones.
            final var emf = Persistence.createEntityManagerFactory("default", Map.ofEntries(
                    Map.entry("hibernate.connection.url", properties.getProperty("database.url")),
                    Map.entry("hibernate.connection.username", username),
                    Map.entry("hibernate.connection.password", password)
            ));
            final var em = emf.createEntityManager();

            // Late replace to prevent reconnecting as anonymous on login failures and allow database username fetching
            disconnectFromDatabase();
            entityManagerFactory = emf;
            entityManager = em;

            user = User.getCurrentFromConnection();
        }
        catch (ServiceException e) {
            if (e.getCause() instanceof GenericJDBCException genericJDBCException) {
                final var text = genericJDBCException.getSQLException().toString();
                if (text.contains("password") && text.contains("fail")) {
                    throw new LoginException("Nieprawidłowe dane logowania!");
                }
            }
            throw e;
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    User user;

    @Override
    public User getUser() {
        if (user == null) {
            return null;
        }
        final var em = getEntityManager();
        if (!em.contains(user)) {
            em.refresh(user);
        }
        return user;
    }
}
