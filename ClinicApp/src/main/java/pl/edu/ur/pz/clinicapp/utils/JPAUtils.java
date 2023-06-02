package pl.edu.ur.pz.clinicapp.utils;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import pl.edu.ur.pz.clinicapp.ClinicApplication;

import javax.persistence.EntityManager;
import java.util.function.Consumer;

public class JPAUtils {
    /**
     * @param object object to work with
     * @return a string representation of the object, or informational string if not initialized.
     */
    static public String toStringWithoutInitializing(Object object) {
        if (Hibernate.isInitialized(object)) {
            return object.toString();
        }
        if (object instanceof HibernateProxy proxy) {
            return "%s ID:%s (not yet initialized by Hibernate)".formatted(
                    proxy.getHibernateLazyInitializer().getEntityName(),
                    proxy.getHibernateLazyInitializer().getEntityName()
            );
        }
        else {
            return "%s (not yet initialized by Hibernate)".formatted(object.getClass());
        }
    }

    /**
     * Provides access to the app entity manager already encapsulated inside read-only transaction.
     * @param action Function (or lambda) to execute with the encapsulated entity manager.
     */
    static public void readOnlyTransaction(Consumer<EntityManager> action) {
        transaction(action, true);
    }

    /**
     * Provides access to the app entity manager already encapsulated inside transaction.
     * @param action Function (or lambda) to execute with the encapsulated entity manager.
     */
    static public void transaction(Consumer<EntityManager> action) {
        transaction(ClinicApplication.getEntityManager(), action, false);
    }

    /**
     * Provides access to the app entity manager already encapsulated inside transaction.
     * @param action Function (or lambda) to execute with the encapsulated entity manager.
     * @param readOnly Whenever the transaction is readonly (could optimise queries).
     */
    static public void transaction(Consumer<EntityManager> action, boolean readOnly) {
        transaction(ClinicApplication.getEntityManager(), action, readOnly);
    }

    /**
     * Encapsulates work with entity manager inside transaction.
     * @param entityManager  Entity manger to work with.
     * @param action Function (or lambda) to execute with the encapsulated entity manager.
     * @param readOnly Whenever the transaction is readonly (could optimise queries).
     */
    static public void transaction(EntityManager entityManager, Consumer<EntityManager> action, boolean readOnly) {
        final var session = entityManager.getTransaction();
        boolean isNew = !session.isActive();
        if (isNew) {
            session.begin();
        }
        if (readOnly) {
            session.setRollbackOnly();
        }
        try {
            action.accept(entityManager);
            if (isNew) {
                if (session.getRollbackOnly()) {
                    session.rollback();
                } else {
                    session.commit();
                }
            }
        }
        catch (Throwable e) {
            if (isNew) {
                if (session.isActive()) {
                    session.rollback();
                }
            }
            throw new RuntimeException("Transaction failed, rolled back", e);
        }
    }
}
