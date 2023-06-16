package pl.edu.ur.pz.clinicapp.models;

/**
 * Common interface for all user references (like {@link User}, {@link Patient} or {@link Doctor}.
 *
 * We cannot use inheritance due to Hibernate, read comments in @{link User} for more info.
 */
public interface UserReference {
    /**
     * @return ID of the user; the same for the base and all the extensions
     */
    Integer getId();

    /**
     * Provides access to concrete {@link User} representation.
     * Might result in error if called unprivileged (i.e. patient loading doctor details).
     * @return concrete representation of the user
     */
    User asUser();
    // TODO: nice exception or null for wrong `asUser` usage (no permissions)?

    /**
     * @return simple string display name for the user
     */
    String getDisplayName();
}
