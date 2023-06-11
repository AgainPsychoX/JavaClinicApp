package pl.edu.ur.pz.clinicapp.utils.javafx;

import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.beans.property.ObjectProperty;
import javafx.util.Duration;

public class Debouncer {
    private final PauseTransition pauseTransition = new PauseTransition();

    public ObjectProperty<Duration> durationProperty() {
        return pauseTransition.durationProperty();
    }
    public Duration getDuration() {
        return pauseTransition.getDuration();
    }
    public void setDuration(Duration duration) {
        pauseTransition.setDuration(duration);
    }

    public boolean isRunning() {
        return pauseTransition.getStatus() == Animation.Status.RUNNING;
    }

    /**
     * Constructs debouncer with default duration of 400 ms for given runnable.
     * @param runnable runnable to debounce
     */
    public Debouncer(Runnable runnable) {
        pauseTransition.setOnFinished(x -> runnable.run());
    }

    /**
     * Constructs debouncer with given duration for given runnable
     * @param duration duration to debounce for
     * @param runnable runnable to debounce
     */
    public Debouncer(Duration duration, Runnable runnable) {
        pauseTransition.setOnFinished(x -> runnable.run());
        pauseTransition.setDuration(duration);
    }

    public void call() {
        pauseTransition.playFromStart();
    }
}
