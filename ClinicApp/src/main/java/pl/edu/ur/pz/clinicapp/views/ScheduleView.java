package pl.edu.ur.pz.clinicapp.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.controls.WeekPane;
import pl.edu.ur.pz.clinicapp.controls.WeekPaneFreeSelectionModel;
import pl.edu.ur.pz.clinicapp.controls.WeekPaneScheduleEntryCell;
import pl.edu.ur.pz.clinicapp.dialogs.ScheduleSlotPickerDialog;
import pl.edu.ur.pz.clinicapp.models.*;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;
import pl.edu.ur.pz.clinicapp.utils.InteractionGuard;

import java.net.URL;
import java.time.*;
import java.util.ResourceBundle;

import static pl.edu.ur.pz.clinicapp.utils.OtherUtils.nullCoalesce;
import static pl.edu.ur.pz.clinicapp.utils.OtherUtils.runDelayed;
import static pl.edu.ur.pz.clinicapp.utils.TemporalUtils.alignDateToWeekStart;

public class ScheduleView extends ChildControllerBase<MainWindowController> implements Initializable {
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
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * State
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * @return date of currently selected week start (monday).
     */
    public LocalDate getDate() {
        return datePicker.getValue();
    }

    /**
     * @return date time of the week pane selection (by entry or by free selector), or current week start date time.
     */
    public LocalDateTime getSelectedDateTime() {
        final var selected =  weekPaneSelectionModel.calculatePotentialDateTimeInWeek(getDate());
        return nullCoalesce(selected, getDate().atStartOfDay());
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
        UserReference userReference = ClinicApplication.getUser();
        var preselectedDate = LocalDate.now();

        if (context.length >= 1) {
            if (context[0] instanceof UserReference x) {
                userReference = x;
            } else {
                throw new IllegalArgumentException();
            }

            if (context.length >= 2) {
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

        schedule = Schedule.of(userReference);

        if (userReference.equals(ClinicApplication.getUser())) {
            headerText.setText("Twój harmonogram");
        } else {
            if (userReference instanceof Doctor doctor && doctor.getSpeciality() != null) {
                headerText.setText("Harmonogram dla %s (%s)".formatted(
                        doctor.getDisplayName(), doctor.getSpeciality()));
            } else {
                headerText.setText("Harmonogram dla %s".formatted(
                        nullCoalesce(userReference.getDisplayName(), userReference.toString())));
            }
        }

        // TODO: if no entries on weekend the 2 columns are hidden; but if current time is weekend,
        //  it would be nice to jump to next week (but do not generate the entries twice; maybe separate query?)
        //  And consider we want to show the weekend columns if we get exact date...
        select(preselectedDate);
    }

    @Override
    public void refresh() {
        select(getDate());
    }

    public void select(LocalDate date) {
        final var weekStartDate = alignDateToWeekStart(date);
        datePicker.setValue(weekStartDate);
        weekPane.setEntries(schedule.generateWeekPaneEntriesForSchedule(weekStartDate));
        weekPaneSelectionModel.clearSelection();
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
                MainWindowController.Views.TIMETABLE,
                getUserReference(),
                TimetableView.Mode.VIEW,
                getSelectedDateTime()
        );
    }

    @FXML
    protected void newVisitAction(ActionEvent actionEvent) {
        getParentController().goToView(
                MainWindowController.Views.VISIT_DETAILS,
                VisitsDetailsView.PrMode.CREATE
        );
        // TODO: allow passing preset info, like date = getSelectedDateTime()

        // FIXME: Temporary testing code for ScheduleSlotPickerDialog,
        //  as I don't have mental at this moment to deal with shit in VisitsDetailsView
        {
            runDelayed(333, () -> {
                final var dialog = new ScheduleSlotPickerDialog(schedule, getSelectedDateTime());
                dialog.setHeaderText("Test 123");
                dialog.showAndWait();
                System.out.println(dialog.getResultDateTime());
                System.out.println(dialog.getResultDuration());
            });
        }
    }

    @FXML
    protected void newEntryAction(ActionEvent actionEvent) {
        // TODO: allow preselect arbitrary time on the week pane
        // TODO: show new simple entry dialog
    }

    @FXML
    protected void detailsAction(ActionEvent actionEvent) {
        final var entry = weekPaneSelectionModel.getSelectedItem();
        if (entry != null) {
            goToEntryDetails(entry);
        }
    }

    protected void goToEntryDetails(WeekPane.Entry entry) {
        if (entry instanceof Appointment appointment) {
            getParentController().goToView(
                    MainWindowController.Views.VISIT_DETAILS,
                    VisitsDetailsView.PrMode.DETAILS,
                    appointment
            );
        } else if (entry instanceof Schedule.SimpleEntry simpleEntry) {
            // TODO: show edit simple entry dialog
            throw new UnsupportedOperationException("Not implemented yet");
        } else if (entry instanceof Schedule.ProxyWeekPaneEntry proxyEntry) {
            goToEntryDetails(proxyEntry.getOriginal());
        }
    }
}
