package pl.edu.ur.pz.clinicapp.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.text.Text;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.controls.WeekPane;
import pl.edu.ur.pz.clinicapp.models.Timetable;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;
import pl.edu.ur.pz.clinicapp.utils.DirtyFixes;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import static pl.edu.ur.pz.clinicapp.utils.OtherUtils.runDelayed;

/* TODO: WIP
 *  modes:
 *      view = show new/edit, hide cancel/save and entries add/edit, lock effective date, lock end date
 *      edit = hide new/edit, show cancel/save and entries add/edit, unlock dates
 *      new = similar to edit
 *  notes:
 *      + Hide date pickers icon button: https://stackoverflow.com/questions/63454031/styling-datepicker-arrowbutton-in-javafx
 *      + Filter date pickers (or make them auto-correct on wrong date): https://stackoverflow.com/questions/35907325/how-to-set-minimum-and-maximum-date-in-datepicker-calander-in-javafx8
 *  steps:
 *      0. map FXML elements as fields
 *      1. base modes switching
 *      2. navigate timetables
 *      3. dialog to add/edit/remove entries (reuse pattern from old project)
 *      4. warning for unsaved changes (add fresh/dirty tracking)
 *      5. double click (or enter on focused) entry to edit
 *      6. properly make use of populate interface
 *      7. finishing touches (like jumping to schedule)
 *  long term steps:
 *      + reuse view as schedule
 *      + early merge
 *      + fix user-patient-doctor dilemmas and permission issues
 *      + SQL side implementation of schedule checks
 *      + unit testing for timetable/schedules
 *      + enforce UI/UX consistency and code quality again (maybe tweak auto-formatting tool?)
 */

public class TimetableView extends ChildControllerBase<MainWindowController> implements Initializable {
    private static final Logger logger = Logger.getLogger(TimetableView.class.getName());

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Elements and initialization
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @FXML protected Text headerText;
    @FXML protected Text totalHoursText;

    @FXML protected Button nextTimetableButton;
    @FXML protected Button previousTimetableButton;

    @FXML protected DatePicker effectiveDatePicker;
    @FXML protected DatePicker endDatePicker;
    @FXML protected Button resetEndDateButton;

    @FXML protected WeekPane<Timetable.Entry> weekPane;

    @FXML protected Button addEntryButton;
    @FXML protected Button editEntryButton;

    @FXML protected Button newTimetableButton;
    @FXML protected Button editTimetableButton;
    @FXML protected Button cancelButton;
    @FXML protected Button saveButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Delay required as the fix below needs already initialized date picker skin
        runDelayed(42, () -> {
            DirtyFixes.fixDatePickerAlwaysEditableViaButton(endDatePicker);
            // no need to fix startDatePicker as it's always editable anyway
        });

        // TODO: set custom entry factory to week pane that informs us about selected/edited entries
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * State
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public enum Mode {
        VIEW,
        NEW,
        EDIT,
    }

    /**
     * Currently selected timetable; Modify using {@link TimetableView#setMode} method to update the UI/
     */
    private Mode mode;

    public Mode getMode() {
        return mode;
    }
    
    protected void setMode(Mode mode) {
        this.mode = mode;
        logger.fine("mode=" + mode);

        switch (mode) {
            case VIEW -> {
                // Lock end date picker
                endDatePicker.setEditable(false);
                resetEndDateButton.setDisable(true);

                // Hide new/edit entries buttons
                addEntryButton.setDisable(true);
                addEntryButton.setVisible(false);
                editEntryButton.setDisable(true);
                editEntryButton.setVisible(false);

                // Fully hide new/edit timetable buttons
                newTimetableButton.setDisable(false);
                newTimetableButton.setVisible(true);
                newTimetableButton.setManaged(true);
                editTimetableButton.setDisable(false);
                editTimetableButton.setVisible(true);
                editTimetableButton.setManaged(true);

                // Fully show cancel/save buttons
                cancelButton.setDisable(true);
                cancelButton.setVisible(false);
                cancelButton.setManaged(false);
                saveButton.setDisable(true);
                saveButton.setVisible(false);
                saveButton.setManaged(false);
            }
            case EDIT, NEW -> {
                // Unlock end date picker
                endDatePicker.setEditable(true);
                resetEndDateButton.setDisable(false);

                // Show new/edit entries buttons
                addEntryButton.setDisable(false);
                addEntryButton.setVisible(true);
                editEntryButton.setDisable(false);
                editEntryButton.setVisible(true);

                // Fully show new/edit timetable buttons
                newTimetableButton.setDisable(true);
                newTimetableButton.setVisible(false);
                newTimetableButton.setManaged(false);
                editTimetableButton.setDisable(true);
                editTimetableButton.setVisible(false);
                editTimetableButton.setManaged(false);

                // Fully hide cancel/save buttons
                cancelButton.setDisable(false);
                cancelButton.setVisible(true);
                cancelButton.setManaged(true);
                saveButton.setDisable(false);
                saveButton.setVisible(true);
                saveButton.setManaged(true);

                // TODO: cancel warning dialog if dirty
            }
        }
    }

    /**
     * Index of currently selected timetable. Modify using {@link TimetableView#select} method to update the UI.
     */
    int selectedTimetableIndex;
    List<Timetable> timetables;
    public Timetable getSelectedTimetable() {
        return timetables.get(selectedTimetableIndex);
    }
    public User getUser() {
        return timetables.get(0).getUser();
    }

    public void select(Timetable timetable) {
        final var index = timetables.indexOf(timetable);
        if (index == -1) {
            throw new IllegalArgumentException();
        }
        select(index);
    }

    public void select(int index) {
        final var count = timetables.size();
        while (index < 0) index += count;
        selectedTimetableIndex = index;

        final var timetable = getSelectedTimetable();
        logger.fine("selectedTimetableIndex=" + index + ", timetable=" + timetable.toString());

        previousTimetableButton.setDisable(index == 0);

        // TODO: update totalHoursText (and keep updating on entries changes)

        effectiveDatePicker.setValue(timetable.getEffectiveDate().toLocalDate());

        if (index < count - 1) {
            nextTimetableButton.setDisable(false);
            final var nextTimetable = timetables.get(index + 1);
            endDatePicker.setValue(nextTimetable.getEffectiveDate().minusDays(1).toLocalDate());
            resetEndDateButton.setDisable(false);
        }
        else {
            nextTimetableButton.setDisable(true);
            endDatePicker.getEditor().setText("zawsze");
        }

        weekPane.setEntries(timetable.getEntries());
        // TODO: scroll week pane to first (or previous of first) row with any entries
    }

    public void selectLatest() {
        select(timetables.size() - 1);
    }

    /**
     * Populates the view for given context.
     *
     * If no argument is provided, the latest timetable for current user will be shown for view. Context arguments:
     * <ol>
     * <li>First argument can specify {@link User} (doctor) or {@link Timetable}.
     * <li>Second argument can specify {@link Mode}.
     * </ol>
     *
     * @param context Optional context arguments.
     */
    @Override
    public void populate(Object... context) {
        var user = ClinicApplication.getUser();
        var mode = Mode.VIEW;
        timetables = null;
        var preselectedIndex = -1;

        if (context.length >= 1) {
            if (context[0] instanceof User x) {
                user = x;
            } else if (context[0] instanceof Timetable x) {
                user = x.getUser();
                timetables = user.getTimetables().stream().sorted().toList();
                preselectedIndex = timetables.indexOf(x);
            } else {
                throw new IllegalArgumentException();
            }

            if (context.length >= 2) {
                if (context[1] instanceof Mode x) {
                    mode = x;
                } else {
                    throw new IllegalArgumentException();
                }
            }
        }

        setMode(mode);

        if (user == ClinicApplication.getUser()) {
            headerText.setText("Twój harmonogram");
        } else {
            // TODO: doctor speciality in braces
            headerText.setText("Harmonogram dla %s".formatted(user.getDisplayName()));
        }

        if (timetables == null) {
            timetables = user.getTimetables().stream().sorted().toList();
        }
        if (timetables.size() == 0) {
            // TODO: for doctors: create empty timetable, start in 'new' mode
            // TODO: for other users: info dialog about not being supported and redirect to other view
            throw new UnsupportedOperationException("Not implemented yet");
        }

        if (preselectedIndex >= 0) {
            select(preselectedIndex);
        } else {
            selectLatest();
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Action handlers
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @FXML
    protected void goNextTimetableAction(ActionEvent event) {
        select((selectedTimetableIndex + 1) % timetables.size());
    }
    @FXML
    protected void goPreviousTimetableAction(ActionEvent event) {
        select((selectedTimetableIndex - 1) % timetables.size());
    }

    @FXML
    protected void effectiveDatePickerAction(ActionEvent actionEvent) {
        // TODO: in view mode: select matching timetable
        // TODO: in edit/add mode: set timetable
    }
    @FXML
    protected void endDatePickerAction(ActionEvent actionEvent) {
        // TODO: in view mode is locked
    }

    @FXML
    protected void resetEndDateAction(ActionEvent actionEvent) {
    }

    @FXML
    protected void goScheduleAction(ActionEvent actionEvent) {
        getParentController().goToView(
                MainWindowController.Views.SCHEDULE,
                getUser(),
                getSelectedTimetable().getEffectiveDate()
        );
    }

    @FXML
    protected void addEntryAction(ActionEvent actionEvent) {
    }

    @FXML
    protected void editEntryButton(ActionEvent actionEvent) {
    }

    @FXML
    protected void newTimetableAction(ActionEvent actionEvent) {
        setMode(Mode.NEW);
    }

    @FXML
    protected void editTimetableAction(ActionEvent actionEvent) {
        setMode(Mode.EDIT);
    }

    @FXML
    protected void cancelAction(ActionEvent actionEvent) {
        if (mode == Mode.NEW) {
            setMode(Mode.VIEW);
            selectLatest();
        } else {
            setMode(Mode.VIEW);
            // stay on the edited one
        }
    }

    @FXML
    protected void saveAction(ActionEvent actionEvent) {
        // TODO: ...!
    }
}
