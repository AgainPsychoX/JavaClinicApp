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

        final var timeSpinnersParent = startTimeSpinner.getParent();
        timeSpinnersParent.setDisable(true);
        timeSpinnersParent.setManaged(false);
        timeSpinnersParent.setVisible(false);
    }
}
