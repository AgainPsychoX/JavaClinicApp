package pl.edu.ur.pz.clinicapp.dialogs;

import pl.edu.ur.pz.clinicapp.models.Appointment;
import pl.edu.ur.pz.clinicapp.models.Doctor;
import pl.edu.ur.pz.clinicapp.models.Schedule;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.logging.Logger;

public class AppointmentSlotPickerDialog extends ScheduleSlotPickerDialog {
    private static final Logger logger = Logger.getLogger(AppointmentSlotPickerDialog.class.getName());

    public AppointmentSlotPickerDialog(Schedule schedule) {
        super(schedule);
    }

    public AppointmentSlotPickerDialog(Schedule schedule, LocalDateTime dateTime) {
        super(schedule, dateTime);
    }

    public AppointmentSlotPickerDialog(Schedule schedule, LocalDateTime beginDateTime, Duration duration) {
        super(schedule, beginDateTime, duration);
    }

    public AppointmentSlotPickerDialog(Schedule schedule, LocalDateTime beginDateTime, LocalDateTime endDateTime) {
        super(schedule, beginDateTime, endDateTime);
    }

    protected Doctor getDoctor() {
        if (schedule.getUserReference() instanceof Doctor doctor) {
            return doctor;
        } else {
            return schedule.getUserReference().asUser().asDoctor();
        }
    }

    @Override
    protected boolean validate() {
        final var minutes = selectionScheduleEntry.getDurationMinutes();
        if (minutes < 5) {
            setExtraTextBelow("Minimalna długość wizyty to 5 minut.");
            return false;
        }
        if (12 * 60 < minutes) {
            setExtraTextBelow("Maksymalna długość wizyty to 12 godzin.");
            return false;
        }

        if (selectionScheduleEntry.doesCrossDays()) {
            setExtraTextBelow("Wizyta nie może być rozłożona na wiele dni.");
            return false;
        }

        // TODO: allow owner & admin unlimited
        final var maxDaysInAdvance = getDoctor().getMaxDaysInAdvance();
        if (selectionScheduleEntry.getEndTime().isAfter(ZonedDateTime.now().plusDays(maxDaysInAdvance).toInstant())) {
            setExtraTextBelow("Doktor nie pozwala umawiać wizyty dalej niż %d dni.".formatted(maxDaysInAdvance));
            return false;
        }

        final var maybeOverlapping = getEarlyOverlapping().filter(e -> e.getType().isBusy()).findAny();
        if (maybeOverlapping.isPresent()) {
            final var overlapping = maybeOverlapping.get();
            logger.finest("Early validation failed, found overlapping: " + overlapping);
            setExtraTextBelow("Wybrany zakres nachodzi na inny wpis.");
            return false;
        }

        final var validationStatus = Appointment.validateNewAppointment(null, getDoctor(),
                selectionScheduleEntry.getBeginTime(), selectionScheduleEntry.getDuration());
        if (validationStatus != Appointment.NewAppointmentValidationStatus.GOOD) {
            logger.finest("Validation failed, error code: " + validationStatus);
            switch (validationStatus) {
                // INVALID_DURATION and TOO_FAR_IN_ADVANCE should be already handled client-side
                case TIMETABLE_CROSSED  -> setExtraTextBelow("Początek i koniec wizyty znajdują się w różnych harmonogramach.");
                case OUTSIDE_TIMETABLE  -> setExtraTextBelow("Wybrany zakres znajduje się poza godzinami harmonogramu");
                case DOCTOR_BUSY        -> setExtraTextBelow("Wybrany zakres nachodzi na inny wpis.");
                default -> {
                    assert false;
                    setExtraTextBelow("");
                }
            }
            return false;
        }

        setExtraTextBelow("");
        return true;
    }
}
