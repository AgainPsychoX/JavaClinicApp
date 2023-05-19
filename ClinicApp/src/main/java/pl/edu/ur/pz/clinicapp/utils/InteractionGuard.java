package pl.edu.ur.pz.clinicapp.utils;

/**
 * Helper class used to avoid recursive looping of events and/or actions
 * handlers for controls that might result in changing values of each other.
 */
public class InteractionGuard {
    private boolean inside = false;

    /**
     * Checks if current execution is within existing interaction;
     * marking beginning itself if it's not.
     *
     * @return true if within existing interaction, false if it is new interaction.
     */
    public boolean begin() {
        if (inside) {
            return true;
        }
        inside = true;
        return false;
    }

    /**
     * Exists current interaction.
     */
    public void end() {
        inside = false;
    }
}
