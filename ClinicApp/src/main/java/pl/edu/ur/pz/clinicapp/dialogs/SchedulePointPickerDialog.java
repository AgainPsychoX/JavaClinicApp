package pl.edu.ur.pz.clinicapp.dialogs;

import pl.edu.ur.pz.clinicapp.models.Schedule;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Dialog for picking point on schedule.
 */
public class SchedulePointPickerDialog extends ScheduleSlotPickerDialog {
    // TODO: shouldn't be the inheritance the other way? didn't want to change much at first, but...

    public SchedulePointPickerDialog(Schedule schedule) {
        this(schedule, null);
    }

    public SchedulePointPickerDialog(Schedule schedule, LocalDateTime dateTime) {
        super(schedule, dateTime, Duration.ZERO);

        setTitle("Wybór punktu w terminarzu");

        // Hide everything but start time spinner - to allow user chose exact time of day
        final var timeSpinnersParent = startTimeSpinner.getParent();
        for (final var child : timeSpinnersParent.getChildrenUnmodifiable()) {
            if (child != startTimeSpinner) {
                child.setDisable(true);
                child.setManaged(false);
                child.setVisible(false);
            }
        }
    }

    @Override
    protected boolean validateSelection() {
        return true;
    }
}
