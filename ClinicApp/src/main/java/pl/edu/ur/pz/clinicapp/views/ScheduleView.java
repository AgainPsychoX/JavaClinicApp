package pl.edu.ur.pz.clinicapp.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;
import org.jetbrains.annotations.Nullable;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.controls.WeekPane;
import pl.edu.ur.pz.clinicapp.controls.WeekPaneFreeSelectionModel;
import pl.edu.ur.pz.clinicapp.controls.WeekPaneScheduleEntryCell;
import pl.edu.ur.pz.clinicapp.dialogs.AppointmentSlotPickerDialog;
import pl.edu.ur.pz.clinicapp.dialogs.ReportDialog;
import pl.edu.ur.pz.clinicapp.dialogs.ScheduleSimpleEntryEditDialog;
import pl.edu.ur.pz.clinicapp.models.*;
import pl.edu.ur.pz.clinicapp.utils.InteractionGuard;
import pl.edu.ur.pz.clinicapp.utils.views.ViewControllerBase;

import java.io.IOException;
import java.net.URL;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;

import static pl.edu.ur.pz.clinicapp.utils.OtherUtils.*;
import static pl.edu.ur.pz.clinicapp.utils.TemporalUtils.alignDateToWeekStart;

public class ScheduleView extends ViewControllerBase implements Initializable {
    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Elements and initialization
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @FXML protected Text headerText;
    @FXML protected Text extraText;
    @FXML protected DatePicker datePicker;
    @FXML protected Button previousWeekButton;
    @FXML protected Button nextWeekButton;

    @FXML protected WeekPane<WeekPane.Entry> weekPane;
    protected WeekPaneFreeSelectionModel<WeekPane.Entry> weekPaneSelectionModel;

    @FXML protected Button goTimetableButton;
    @FXML protected Button newVisitButton;
    @FXML protected Button newEntryButton;
    @FXML protected Button detailsButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        weekPaneSelectionModel = new WeekPaneFreeSelectionModel<>(weekPane);

        // Enable details button only if there is any entry selected
        detailsButton.disableProperty().bind(weekPaneSelectionModel.selectedIndexProperty().isEqualTo(-1));

        weekPane.setEntryCellFactory(weekPane -> new WeekPaneScheduleEntryCell<>() {
            {
                setOnMouseClicked(event -> {
                    event.consume();
                    if (event.getButton() == MouseButton.PRIMARY) {
                        final var entry = getItem();
                        weekPaneSelectionModel.select(entry);
                        if (event.getClickCount() == 2) {
                            goToEntryDetails(entry);
                        }
                    }
                });
            }
        });

        // TODO: abstract up repeated logic (2x TimetableView, 1x here, 1x ScheduleSlotPickerDialog?)
        datePicker.setDayCellFactory(datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                if (!empty) {
                    final var zonedDateTime = item.atStartOfDay(ZoneId.systemDefault());
                    final var index = findEffectiveTimetableIndex(zonedDateTime);

                    getStyleClass().removeIf(s -> s.startsWith("fancy"));
                    if (index != -1) {
                        getStyleClass().add("fancy" + (index % 9 + 1));
                    }
                }
            }
        });
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * State
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Used for colouring the date picker.
     */
    private List<ZonedDateTime> timetableEffectiveDates;

    private int findEffectiveTimetableIndex(ZonedDateTime givenDate) {
        for (int i = timetableEffectiveDates.size() - 1; i >= 0; i--) {
            final var effectiveDate = timetableEffectiveDates.get(i);
            if (!givenDate.isBefore(effectiveDate)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @return date of currently selected week start (monday).
     */
    public LocalDate getDate() {
        return datePicker.getValue();
    }

    /**
     * @return date time of the week pane selection (by entry or by free selector), null if no selection.
     */
    public LocalDateTime getSelectedDateTime() {
        return weekPaneSelectionModel.calculatePotentialDateTimeInWeek(getDate());
    }

    protected Schedule schedule;

    /**
     * Returns reference to owner of the schedule.
     * @return Reference to user that is owns the schedule.
     */
    public UserReference getUserReference() {
        return schedule.getUserReference();
    }

    /**
     * Populates the view for given context.
     *
     * If no argument is provided, the schedule for current user for current week will be shown. Context arguments:
     * <ol>
     * <li>First argument can specify {@link UserReference} (doctor).
     * <li>Second argument can specify {@link LocalDate}.
     * </ol>
     *
     * @param context Optional context arguments.
     */
    @Override
    public void populate(Object... context) {
        final var loggedInUser = ClinicApplication.requireUser();
        UserReference userReference = loggedInUser;
        var preselectedDate = LocalDate.now();

        if (context.length > 0) {
            if (context[0] instanceof UserReference x) {
                userReference = x;
            } else {
                throw new IllegalArgumentException();
            }

            if (context.length > 1) {
                if (context[1] instanceof LocalDate y) {
                    preselectedDate = y;
                } else if (context[1] instanceof ZonedDateTime y) {
                    preselectedDate = y.toLocalDate();
                } else if (context[1] instanceof Instant y) {
                    preselectedDate = y.atZone(ZoneId.systemDefault()).toLocalDate();
                } else {
                    throw new IllegalArgumentException();
                }
            }
        }

        if (userReference == null) {
            throw new IllegalStateException();
        }

        if (userReference.equals(loggedInUser)) {
            schedule = Schedule.of(loggedInUser);
            headerText.setText("Twój harmonogram");
        } else {
            schedule = Schedule.of(userReference);
            if (userReference instanceof Doctor doctor && doctor.getSpeciality() != null) {
                headerText.setText("Harmonogram dla %s (%s)".formatted(
                        doctor.getDisplayName(), doctor.getSpeciality()));
            } else {
                headerText.setText("Harmonogram dla %s".formatted(
                        nullCoalesce(userReference.getDisplayName(), userReference.toString())));
            }
        }

        extraText.setVisible(false); // TODO: use it somehow?

        // TODO: if no entries on weekend the 2 columns are hidden; but if current time is weekend,
        //  it would be nice to jump to next week (but do not generate the entries twice; maybe separate query?)
        //  And consider we want to show the weekend columns if we get exact date...
        select(preselectedDate);

        timetableEffectiveDates = schedule.getCachedTimetables().stream().map(Timetable::getEffectiveDate).toList();
    }

    @Override
    public void refresh() {
        select(getDate());

        timetableEffectiveDates = schedule.getCachedTimetables().stream().map(Timetable::getEffectiveDate).toList();
    }

    public void select(LocalDate date) {
        final var weekStartDate = alignDateToWeekStart(date);
        datePicker.setValue(weekStartDate);
        weekPane.setEntries(schedule.generateWeekPaneEntriesForSchedule(weekStartDate));
        weekPaneSelectionModel.clearSelection();
        weekPane.displayDatesInHeader(weekStartDate);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Action handlers
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private final InteractionGuard interactionGuard = new InteractionGuard();

    @FXML
    protected void datePickerAction(ActionEvent actionEvent) {
        if (interactionGuard.begin()) return;
        select(datePicker.getValue());
        interactionGuard.end();
    }

    @FXML
    protected void goPreviousWeekAction(ActionEvent actionEvent) {
        datePicker.setValue(getDate().minusDays(7)); // will also cause `datePickerAction`
    }

    @FXML
    protected void goNextWeekAction(ActionEvent actionEvent) {
        datePicker.setValue(getDate().plusDays(7)); // will also cause `datePickerAction`
    }

    @FXML
    protected void goTimetableAction(ActionEvent actionEvent) {
        getParentController().goToView(
                TimetableView.class,
                getUserReference(),
                TimetableView.Mode.VIEW,
                nullCoalesce(getSelectedDateTime(), getDate().atStartOfDay())
        );
    }

    @FXML
    protected void newVisitAction(ActionEvent actionEvent) {
        getParentController().goToView(
                VisitsDetailsView.class,
                VisitsDetailsView.Mode.CREATE
        );
        // TODO: allow passing preset info, like date = getSelectedDateTime()

        // FIXME: Temporary testing code for ScheduleSlotPickerDialog,
        //  as I don't have mental at this moment to deal with shit in VisitsDetailsView
        {
            runDelayed(333, () -> {
                final var dialog = new AppointmentSlotPickerDialog(
                        schedule, nullCoalesce(getSelectedDateTime(), getDate().atStartOfDay()));
                dialog.showAndWait();
                System.out.println(dialog.getResult());
            });
        }
    }

    @FXML
    protected void newEntryAction(ActionEvent actionEvent) {
        showAddOrEditSimpleEntryDialog(null);
    }

    @FXML
    protected void detailsAction(ActionEvent actionEvent) {
        final var entry = weekPaneSelectionModel.getSelectedItem();
        if (entry != null) {
            goToEntryDetails(entry);
        }
    }

    protected void goToEntryDetails(WeekPane.Entry weekPaneEntry) {
        if (weekPaneEntry instanceof Schedule.ScheduleWeekPaneEntry proxy) {
            final var scheduleEntry = proxy.getScheduleEntry();
            if (scheduleEntry instanceof Appointment appointment) {
                getParentController().goToView(
                        VisitsDetailsView.class,
                        VisitsDetailsView.Mode.DETAILS,
                        appointment
                );
            } else if (scheduleEntry instanceof Schedule.SimpleEntry simpleEntry) {
                switch (simpleEntry.getType()) {
                    case NONE -> {}
                    case OPEN -> {
                        if (requireConfirmation(
                                Alert.AlertType.CONFIRMATION,
                                "Potwierdzenie przejścia do harmonogramu", """
                                Ten wpis pochodzi z ustawień harmonogramu. W celu jego edycji, nastąpi przejście do
                                widoku harmonogramu. Czy chcesz kontynuować?
                                """, ButtonType.YES, ButtonType.CANCEL)) {
                            getParentController().goToView(
                                    TimetableView.class,
                                    getUserReference(),
                                    TimetableView.Mode.EDIT,
                                    simpleEntry.getBeginInstant()
                                    // TODO: select specific timetable entry
                            );
                        }
                    }
                    default -> showAddOrEditSimpleEntryDialog(proxy);
                }
            }
        }
    }

    protected void showAddOrEditSimpleEntryDialog(@Nullable Schedule.ScheduleWeekPaneEntry weekPaneEntry) {
        // TODO: warning about editing past/already expired time?

        /* Persisting changes is managed by the dialog itself here.
         */
        ScheduleSimpleEntryEditDialog dialog;
        if (weekPaneEntry == null) {
            dialog = new ScheduleSimpleEntryEditDialog(null, schedule);
            dialog.populate(nullCoalesce(getSelectedDateTime(), LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)));
        } else {
            dialog = new ScheduleSimpleEntryEditDialog((Schedule.SimpleEntry) weekPaneEntry.getScheduleEntry(), schedule);
        }
        dialog.showAndWait();
        switch (dialog.getState()) {
            case NEW_COMMITTED -> {
                weekPane.getEntries().add(new Schedule.ScheduleWeekPaneEntry(dialog.getEntry()));
            }
            case EDIT_COMMITTED -> {
                weekPane.refreshEntry(weekPaneEntry);
            }
            case DELETE_COMMITTED -> {
                weekPane.getEntries().remove(weekPaneEntry);
            }
        }
    }

    /**
     * Method for printing timetable report
     * @param actionEvent Action event
     * @throws IOException When report file is not found
     */
    @FXML
    public void printTimetableAction(ActionEvent actionEvent) throws IOException {
        ReportDialog rd = new ReportDialog();
        ReportDialog.createConfig();
        rd.initialize(null, null);
        rd.timetableReport(weekPane, datePicker.getValue());
    }
}
