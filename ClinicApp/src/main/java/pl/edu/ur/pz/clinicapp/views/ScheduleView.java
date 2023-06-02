package pl.edu.ur.pz.clinicapp.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.controls.WeekPane;
import pl.edu.ur.pz.clinicapp.controls.WeekPaneSelectionModel;
import pl.edu.ur.pz.clinicapp.models.Appointment;
import pl.edu.ur.pz.clinicapp.models.Doctor;
import pl.edu.ur.pz.clinicapp.models.Schedule;
import pl.edu.ur.pz.clinicapp.models.UserReference;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;
import pl.edu.ur.pz.clinicapp.utils.InteractionGuard;

import java.net.URL;
import java.time.*;
import java.util.ResourceBundle;

import static pl.edu.ur.pz.clinicapp.utils.OtherUtils.nullCoalesce;

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
    protected WeekPaneSelectionModel<WeekPane.Entry> weekPaneSelectionModel; // TODO: need custom here?

    @FXML protected Button goTimetableButton;
    @FXML protected Button newVisitButton;
    @FXML protected Button newEntryButton;
    @FXML protected Button detailsButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        weekPaneSelectionModel = new WeekPaneSelectionModel<>(weekPane);

        // Enable details button only if there is any entry selected
        detailsButton.disableProperty().bind(weekPaneSelectionModel.selectedIndexProperty().isEqualTo(-1));

        weekPane.setEntryCellFactory(weekPane -> new WeekPane.EntryCell<>() {
            {
                setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        final var entry = getItem();
                        weekPaneSelectionModel.select(entry);
                        if (event.getClickCount() == 2) {
                            goToEntryDetails(entry);
                        }
                    }
                });
            }

            @Override
            public void updateItem(WeekPane.Entry item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().retainAll("cell", "entry"); // FIXME: set defaults

                if (empty || item == null) {
                    setText("?");
                }
                else {
                    // TODO: if isTall & isWide -> 8:00 - 8:15 (15 minut)\nMarin Kowalski ?
                    if (item instanceof Appointment appointment) {
                        final var patient = appointment.getPatient();
                        setTextAlignment(TextAlignment.LEFT);
                        setText("%s %s. %s".formatted(
                                appointment.startAsLocalTime().toString().replaceFirst("^0+(?!$)", ""),
                                patient.getName().charAt(0), patient.getSurname()
                        ));
                    } else if (item instanceof Schedule.SimpleEntry
                            || item instanceof Schedule.ProxyWeekPaneEntry) {
                        final var entry = (Schedule.Entry) item;
                        setTextAlignment(TextAlignment.CENTER);
                        setText("(" + entry.getType().localizedName() + ")");
                    } else {
                        assert false;
                    }
                }
            }
        });
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * State
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

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

        select(preselectedDate);
    }

    @Override
    public void refresh() {
        select(getDate());
    }

    public LocalDate getDate() {
        return datePicker.getValue();
    }

    LocalDate alignDateToWeekStart(LocalDate date) {
        assert DayOfWeek.MONDAY.ordinal() == 0; // always true
        return date.minusDays(date.getDayOfWeek().ordinal());
    }

    LocalDate alignDateToWeekEnd(LocalDate date) {
        assert DayOfWeek.SUNDAY.ordinal() == 6; // always true
        return date.plusDays(7 - date.getDayOfWeek().ordinal());
    }

    public void select(LocalDate date) {
        final var weekStartDate = alignDateToWeekStart(date);
        datePicker.setValue(weekStartDate);
        weekPane.setEntries(schedule.generateWeekPaneEntriesForSchedule(weekStartDate));
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
                getDate() // TODO: use date of selected entry if avaliable
        );
    }

    @FXML
    protected void newVisitAction(ActionEvent actionEvent) {
        // TODO: allow preselect arbitrary time on the week pane

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
