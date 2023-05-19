package pl.edu.ur.pz.clinicapp.views;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.controls.WeekPane;
import pl.edu.ur.pz.clinicapp.dialogs.TimetableEntryEditDialog;
import pl.edu.ur.pz.clinicapp.models.Timetable;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;
import pl.edu.ur.pz.clinicapp.utils.DirtyFixes;
import pl.edu.ur.pz.clinicapp.utils.InteractionGuard;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.logging.Logger;

import static pl.edu.ur.pz.clinicapp.utils.JPAUtils.transaction;
import static pl.edu.ur.pz.clinicapp.utils.OtherUtils.*;

/* TODO: WIP
 *  modes:
 *      view = show new/edit, hide cancel/save and entries add/edit, lock effective date, lock end date
 *      edit = hide new/edit, show cancel/save and entries add/edit, unlock dates
 *      new = similar to edit
 *  notes:
 *      + Hide date pickers icon button: https://stackoverflow.com/questions/63454031/styling-datepicker-arrowbutton-in-javafx
 *      + Filter date pickers (or make them auto-correct on wrong date): https://stackoverflow.com/questions/35907325/how-to-set-minimum-and-maximum-date-in-datepicker-calander-in-javafx8
 *  steps:
 *      0. map FXML elements as fields -- DONE
 *      1. base modes switching -- DONE
 *      2. navigate timetables --- DONE
 *      3. dialog to add/edit/remove entries (reuse pattern from old project) --- DONE? (test editing)
 *      4. warning for unsaved changes (add fresh/dirty tracking)
 *      5. double click (or enter on focused) entry to edit
 *      6. properly make use of populate interface -- DONE?
 *      7. finishing touches (like jumping to schedule)
 *  long term steps:
 *      + new timetable should base of the current selected one?
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

    @FXML protected Button previousTimetableButton;
    @FXML protected Button nextTimetableButton;

    @FXML protected DatePicker effectiveDatePicker;
    @FXML protected DatePicker endDatePicker;
    @FXML protected Button resetEndDateButton;

    @FXML protected WeekPane<Timetable.Entry> weekPane;

    @FXML protected Button addEntryButton;
    @FXML protected Button editEntryButton;

    @FXML protected Button deleteTimetableButton;
    @FXML protected Button newTimetableButton; // always visible (view mode: encourages to edit, acts as shortcut)
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

        effectiveDatePicker.setDayCellFactory(param -> new DateCell() {
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

        // TODO: custom date pickers factories:
        //  start (effective) -> ? (DONE?)
        //  end -> disallow changing before start date
        //  + maybe show timetables by colors? (DONE)
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * State
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public enum Mode {
        VIEW,
        EDIT,
    }

    /**
     * Currently selected timetable.
     */
    public ReadOnlyObjectProperty<Mode> modeProperty() {
        return mode;
    }
    private final ReadOnlyObjectWrapper<Mode> mode = new ReadOnlyObjectWrapper<>();
    public Mode getMode() {
        return mode.get();
    }

    private void setButtonEnabledVisibleManaged(Button button, boolean show) {
        button.setDisable(!show);
        button.setVisible(show);
        button.setManaged(show);
    }
    
    protected void setMode(Mode mode) {
        this.mode.set(mode);
        logger.fine("Mode: " + mode);

        switch (mode) {
            case VIEW -> {
                // Lock end date picker
                endDatePicker.setEditable(false);
                resetEndDateButton.setDisable(true);

                // Hide/show buttons
                setButtonEnabledVisibleManaged(addEntryButton, false);
                setButtonEnabledVisibleManaged(editEntryButton, false);
                setButtonEnabledVisibleManaged(deleteTimetableButton, false);
                setButtonEnabledVisibleManaged(editTimetableButton, true);
                setButtonEnabledVisibleManaged(cancelButton, false);
                setButtonEnabledVisibleManaged(saveButton, false);
            }
            case EDIT -> {
                // Unlock end date picker
                endDatePicker.setEditable(true);
                resetEndDateButton.setDisable(false);

                // Show/hide buttons
                setButtonEnabledVisibleManaged(addEntryButton, true);
                setButtonEnabledVisibleManaged(editEntryButton,true);
                setButtonEnabledVisibleManaged(deleteTimetableButton, true);
                setButtonEnabledVisibleManaged(editTimetableButton, false);
                setButtonEnabledVisibleManaged(cancelButton, true);
                setButtonEnabledVisibleManaged(saveButton, true);
            }
        }
    }

    /**
     * List of timetables, kept sorted chronologically (oldest first).
     */
    private List<Timetable> timetables = Collections.emptyList();

    /**
     * Index of currently selected timetable. Modify using {@link TimetableView#select} method to update the UI.
     */
    private int currentTimetableIndex;

    /**
     * Returns index of current timetable in internal view list of timetables (chronologically ordered).
     * @return index of current timetable.
     */
    public int getCurrentTimetableIndex() {
        return currentTimetableIndex;
    }

    /**
     * Returns currently selected timetable.
     * @return Currently selected timetable.
     */
    public Timetable getTimetable() {
        return timetables.get(currentTimetableIndex);
    }

    /**
     * Returns owner of the timetable(s). We assume every timetable in the view is related to the same user.
     * @return User that is owns the timetable(s).
     */
    public User getUser() {
        return timetables.get(0).getUser();
    }

    /**
     * Returns index of previous timetable in internal view list of timetables (chronologically ordered).
     * @return index of previous timetable (if any) or -1 if there is no previous timetable to current one.
     */
    public int getPreviousTimetableIndex() {
        return currentTimetableIndex - 1;
    }

    /**
     * Returns previous timetable (chronologically by effective date), if any.
     * @return timetable if there is previous to current one, or null otherwise.
     */
    public Timetable getPreviousTimetable() {
        final int index = getPreviousTimetableIndex();
        return index < 0 ? null : timetables.get(index);
    }

    /**
     * Returns index of next timetable in internal view list of timetables (chronologically ordered).
     * @return index of next timetable (if any) or -1 if there is no next timetable to current one.
     */
    public int getNextTimetableIndex() {
        final var index = currentTimetableIndex + 1;
        return index == timetables.size() ? -1 : index;
    }

    /**
     * Returns next timetable (chronologically by effective date), if any.
     * @return timetable if there is next to current one, or null otherwise.
     */
    public Timetable getNextTimetable() {
        final int index = getNextTimetableIndex();
        return index < 0 ? null : timetables.get(index);
    }

    /**
     * Finds effective timetable as index for given date.
     * @param date date to look around.
     * @return index for found timetable, or -1 if not found.
     */
    public int findEffectiveTimetableIndex(ZonedDateTime date) {
        // Looping in reverse order through sorted chronologically timetables
        for (int i = timetables.size() - 1; i >= 0; i--) {
            final var timetable = timetables.get(i);
            if (!date.isBefore(timetable.getEffectiveDate())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds effective timetable for given date.
     * @param date date to look around.
     * @return found timetable, or nul if not found.
     */
    public Timetable findEffectiveTimetable(ZonedDateTime date) {
        final var index = findEffectiveTimetableIndex(date);
        return index < 0 ? null : timetables.get(index);
    }

    public void select(Timetable timetable) {
        final var index = timetables.indexOf(timetable);
        if (index == -1) {
            throw new IllegalArgumentException();
        }
        select(index);
    }

    public void select(ZonedDateTime date) {
        final var index = findEffectiveTimetableIndex(date);
        if (index == -1) {
            // Silent error, falls back to oldest timetable
            logger.warning("Cannot find timetable to select for date %s".formatted(date));
            select(0);
        }
        else {
            select(index);
        }
    }

    private static final NumberFormat totalHoursNumberFormat = new DecimalFormat("#.##");

    public void select(int index) {
        final var count = timetables.size();
        while (index < 0) index += count;
        currentTimetableIndex = index;

        final var timetable = getTimetable();
        logger.finer("Selecting index: " + index + ", timetable: " + timetable);

        previousTimetableButton.setDisable(index == 0);
        effectiveDatePicker.setValue(timetable.getEffectiveDate().toLocalDate());
        totalHoursText.setText("Liczba godzin tygodniowo: " +
                totalHoursNumberFormat.format((float) timetable.getTotalMinutesWeekly() / 60));

        final var nextTimetable = getNextTimetable();
        if (nextTimetable != null) {
            nextTimetableButton.setDisable(false);
            endDatePicker.setValue(nextTimetable.getEffectiveDate().toLocalDate());
            resetEndDateButton.setDisable(false); // in view mode always disabled
        }
        else {
            nextTimetableButton.setDisable(true);
            endDatePicker.setValue(null);
            if (getMode() != Mode.VIEW) {
                resetEndDateButton.setDisable(true); // in view mode always disabled
            }
        }

        deleteTimetableButton.setDisable(index == 0);

        weekPane.setEntries(timetable.getEntries());
        // TODO: scroll week pane to first (or previous of first) row with any entries
    }

    /**
     * Populates the view for given context.
     *
     * If no argument is provided, the latest timetable for current user will be shown for view. Context arguments:
     * <ol>
     * <li>First argument can specify {@link User} (doctor) or {@link Timetable}.
     * <li>Second argument can specify {@link Mode}.
     * <li>Third argument can specify {@link ZonedDateTime}, to select effective timetable for given date.
     * </ol>
     *
     * @param context Optional context arguments.
     */
    @Override
    public void populate(Object... context) {
        var user = ClinicApplication.requireUser();
        var mode = Mode.VIEW;
        timetables = null;
        var preselectedIndex = -1;
        ZonedDateTime preselectedDate = null;

        if (context.length >= 1) {
            if (context[0] instanceof User x) {
                user = x;
            } else if (context[0] instanceof Timetable x) {
                user = x.getUser();
                timetables = new ArrayList<>(user.getTimetables());
                timetables.sort(Comparator.comparing(Timetable::getEffectiveDate));
                preselectedIndex = timetables.indexOf(x);
            } else {
                throw new IllegalArgumentException();
            }

            if (context.length >= 2) {
                if (context[1] instanceof Mode x) {
                    mode = x;
                } else if (context[1] instanceof Boolean x) {
                    // Without this 'else if' the x variable is leaked. Why Java is so confusing?
                    doNothing(x);
                } else {
                    throw new IllegalArgumentException();
                }

                if (context.length >= 3) {
                    if (context[2] instanceof ZonedDateTime x) {
                        preselectedDate = x;
                    } else {
                        throw new IllegalArgumentException();
                    }
                }
            }
        }

        if (timetables == null) {
            timetables = new ArrayList<>(user.getTimetables());
            timetables.sort(Comparator.comparing(Timetable::getEffectiveDate));
        }
        if (timetables.size() == 0) {
            // TODO: for doctors: create empty timetable, start in 'new' mode
            // TODO: for other users: info dialog about not being supported and redirect to other view
            throw new UnsupportedOperationException("Not implemented yet");
        }

        final var entityManager = ClinicApplication.getEntityManager();
        for (var timetable : timetables) {
            entityManager.detach(timetable);
        }

        setMode(mode);

        if (user == ClinicApplication.getUser()) {
            headerText.setText("Twój harmonogram");
        } else {
            // TODO: doctor speciality in braces
            headerText.setText("Harmonogram dla %s".formatted(
                    nullCoalesce(user.getDisplayName(), user.getDatabaseUsername())));
        }

        if (preselectedDate != null) {
            select(preselectedDate);
        } else if (preselectedIndex >= 0) {
            select(preselectedIndex);
        } else {
            select(ZonedDateTime.now());
        }
    }

    /**
     * Sorts timetables chronologically then (re)selects specified timetable.
     * To be used after adding/moving timetables. Coupled with select to address index invalidation
     * after sorting and update the UI.
     * @param timetable timetable to be selected after reordering.
     */
    protected void reorderThenSelect(Timetable timetable) {
        logger.fine("Reordering...");
        timetables.sort(Comparator.comparing(Timetable::getEffectiveDate));
        select(timetable);
    }

    protected void repopulate() {
        logger.fine("Repopulating...");
        final var entityManager = ClinicApplication.getEntityManager();
        final var currentDate = getTimetable().getEffectiveDate();
        final var user = getUser();

        /* If cascading on User towards Timetable(s) collection were to be enabled one could expect everything
         * to be refreshed nicely, however N+1 (or more) queries happen, so cascading is disabled. In such case,
         * refreshing user recreates the related collection (timetables), and next time it is initialized it's done
         * with custom prefetch (again, to avoid N+1, see User::getTimetables).
         *
         * So far my experience Hibernate feels like communism - good idea, but fails once you want to use it
         * in it fullest. Fetches never joining tables on its own (despite EAGER fetching or JOIN fetch mode);
         * objective approach seems to be a lie - properties and collection proxies magic crafted with reflection,
         * yet still requiring boilerplate. Some people online suggest to compose raw entities classes into smarter
         * objects that iron out Hibernate being unintuitive or straight up stupid, allowing for access to the data
         * and operations related to the entity (active record pattern). Other pattern recommended is to use separate
         * classes to handle all actions while using the entity object only as raw data (DAO pattern).
         *
         * So far neither was chosen to avoid complexity, but I think about active pattern, as hacking stuff to work
         * (like User::getTimetables or here) is just as intuitive itself as Hibernate in the long run...
         * TODO: consider using Active Record Object or DAO pattern to deal with Hibernate being annoying
         */
        entityManager.refresh(user);

        populate(user, getMode(), currentDate);
    }

    @Override
    public void refresh() {
        logger.fine("Refreshing...");
        final var date = getTimetable().getEffectiveDate();

        if (getMode() == Mode.EDIT) {
            // TODO: ask only when dirty
            if (requireConfirmation("Niezapisane zmiany",
                    "Zmiany nie zostały jeszcze zapisane. " +
                            "Czy na pewno chcesz kontynuować i odświeżyć dane?",
                    ButtonType.CANCEL)) {
                setMode(Mode.VIEW);
            }
            else {
                return;
            }
        }

        repopulate();
        select(date); // just try to hit the same timetable, no promises
    }

    protected void cancelForce() {
        setMode(Mode.VIEW);
        repopulate(); // TODO: only if dirty?
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Action handlers
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private final InteractionGuard interactionGuard = new InteractionGuard();

    @FXML
    protected void goPreviousTimetableAction(ActionEvent event) {
        if (interactionGuard.begin()) return;
        select(getPreviousTimetableIndex());
        interactionGuard.end();
    }
    @FXML
    protected void goNextTimetableAction(ActionEvent event) {
        if (interactionGuard.begin()) return;
        select(getNextTimetableIndex());
        interactionGuard.end();
    }

    @FXML
    protected void effectiveDatePickerAction(ActionEvent actionEvent) {
        if (interactionGuard.begin()) return;

        if (getMode() == Mode.VIEW) {
            select(effectiveDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()));
        }
        else /* Mode.NEW or Mode.EDIT */ {
            // TODO: warning about problems with editing past/already effective timetable,
            //  prefer adding new (only once in edit session)

            // TODO: check previous timetables:
            //  + confirm (allow remember bc low invasive) about prior timetable effective end being changed,
            //      if forward -> allow creating empty timetable?
            //  + if new date would move the timetable prior/after other timetable -> confirm

            // TODO: check the code before testing once more, make intrusive choices non-default.

            final var timetable = getTimetable();
            final var newDate = effectiveDatePicker.getValue().atStartOfDay(ZoneId.systemDefault());

            if (newDate.isBefore(timetable.getEffectiveDate())) /* new date before current one */ {
                final var previousTimetable = getPreviousTimetable();
                if (newDate.isBefore(previousTimetable.getEffectiveDate())) /* reordering case */ {
                    // TODO: allow remember, but only the less intrusive choice
                    // TODO: one more choice possible: move but create empty timetable instead extending the source
                    //  previous one, but it's edge case of edge bases and I'm confused enough already...
                    final var dialog = new Alert(Alert.AlertType.CONFIRMATION);
                    dialog.setTitle("Ustawienie daty efektywnej");
                    dialog.setHeaderText(null);
                    dialog.setContentText(
                            "Nowa data poprzedza inne wzory harmonogramu. Spowoduje to przesunięcie obecnego " +
                            "harmonogramu z zmianę jego daty końca na datę docelowo kolejnego harmonogramu," +
                            "a także zmianę daty końca docelowego poprzednika na nową datę.\n\n" +
                            "Alternatywnie, wzory harmonogramów pomiędzy nową datą efektywną a obecną datą końca " +
                            "mogą być nadpisane (zostaną utracone).");
                    dialog.getButtonTypes().setAll(
                            ButtonType.YES,
                            new ButtonType("Nadpisz", ButtonBar.ButtonData.OTHER),
                            ButtonType.CANCEL);
                    final var choice = dialog.showAndWait().orElse(ButtonType.CLOSE);
                    switch (choice.getButtonData()) {
                        case YES -> {
                            timetable.setEffectiveDate(newDate);
                            reorderThenSelect(timetable);
                        }
                        case OTHER -> {
                            final var toBeRemoved = new ArrayList<Timetable>(10);
                            for (int i = 0; i < currentTimetableIndex; i++) {
                                final var otherTimetable = timetables.get(i);
                                if (otherTimetable.getEffectiveDate().isAfter(newDate)) {
                                    toBeRemoved.add(otherTimetable);
                                }
                            }
                            timetables.removeAll(toBeRemoved);
                            timetable.setEffectiveDate(newDate);
                            select(timetable); // reorder not needed, but update UI in case there is no previous table
                        }
                        default /* incl. cancel & close */ -> {
                            // Reject the change
                            effectiveDatePicker.setValue(timetable.getEffectiveDate().toLocalDate());
                        }
                    }
                }
                else /* easy case */ {
                    // TODO: allow remember
                    if (requireConfirmation("Ustawienie daty efektywnej",
                            ("Nowa data efektywna zachodzi na poprzedni wzór harmonogramu. Spowoduje to jego " +
                             "skrócenie poprzez ustawienie końca na nową datę. Czy chcesz kontynuować?"),
                            ButtonType.CANCEL)) {
                        getTimetable().setEffectiveDate(newDate);
                    }
                    else {
                        // Reject the change
                        effectiveDatePicker.setValue(timetable.getEffectiveDate().toLocalDate());
                    }
                }
            }
            else /* new date after current one */ {
                final var nextTimetable = getNextTimetable();
                if (newDate.isBefore(nextTimetable.getEffectiveDate())) /* easy case */ {
                    // TODO: allow remember bc low invasive
                    final var dialog = new Alert(Alert.AlertType.CONFIRMATION);
                    dialog.setTitle("Ustawienie daty efektywnej");
                    dialog.setHeaderText(null);
                    dialog.setContentText(
                            ("Zmiana daty efektywnej do przodu spowoduje wydłużenie się poprzedniego harmonogramu. " +
                             "Czy chcesz kontynuować? \n\n" +
                             "Alternatywnie, może zostać utworzony pusty harmonogram w luce (od %s do %s).")
                                    .formatted(timetable.getEffectiveDate(), newDate));
                    dialog.getButtonTypes().setAll(
                            new ButtonType("Zmień", ButtonBar.ButtonData.YES),
                            new ButtonType("Nowy pusty", ButtonBar.ButtonData.OTHER),
                            ButtonType.CANCEL);
                    final var choice = dialog.showAndWait().orElse(ButtonType.CLOSE);
                    switch (choice.getButtonData()) {
                        case YES -> {
                            timetable.setEffectiveDate(newDate);
                        }
                        case OTHER -> {
                            // Add new empty timetable right after this one
                            final var newTimetable = new Timetable(timetable.getEffectiveDate());
                            timetables.add(currentTimetableIndex, newTimetable);
                            // No need to update UI nor selection.

                            timetable.setEffectiveDate(newDate);
                            select(timetable); // valid order, but update index
                        }
                        default /* incl. cancel & close */ -> {
                            // Reject the change
                            effectiveDatePicker.setValue(timetable.getEffectiveDate().toLocalDate());
                        }
                    }
                }
                else /* reordering case */ {
                    final var dialog = new Alert(Alert.AlertType.CONFIRMATION);
                    dialog.setTitle("Ustawienie daty efektywnej");
                    dialog.setHeaderText(null);
                    dialog.setContentText(
                            ("Nowa data efektywna przekracza inne kolejne harmonogramy. Spowoduje to " +
                             "ustawienie daty końca ostatniego z nich na nową datę, " +
                             "oraz daty końca obecnego poprzednika na obecną datę końca. Czy chcesz kontynuować? \n\n" +
                             "Alternatywnie, może zostać utworzony pusty harmonogram w powstałej luce (od %s do %s).")
                                    .formatted(timetable.getEffectiveDate(), nextTimetable.getEffectiveDate()));
                    dialog.getButtonTypes().setAll(
                            new ButtonType("Przedłuż poprzednik", ButtonBar.ButtonData.YES),
                            new ButtonType("Wypełnij lukę pustym", ButtonBar.ButtonData.OTHER),
                            ButtonType.CANCEL);
                    final var choice = dialog.showAndWait().orElse(ButtonType.CLOSE);
                    switch (choice.getButtonData()) {
                        case YES -> {
                            timetable.setEffectiveDate(newDate);
                            reorderThenSelect(timetable);
                        }
                        case OTHER -> {
                            final var newTimetable = new Timetable(timetable.getEffectiveDate());
                            timetables.add(currentTimetableIndex, newTimetable);

                            timetable.setEffectiveDate(newDate);
                            reorderThenSelect(timetable);
                        }
                        default /* incl. cancel & close */ -> {
                            // Reject the change
                            effectiveDatePicker.setValue(timetable.getEffectiveDate().toLocalDate());
                        }
                    }
                }
            }
        }

        interactionGuard.end();
    }

    @FXML
    protected void endDatePickerAction(ActionEvent actionEvent) {
        /* General rule here is that end picker changes SHOULD NOT ever change effective date of the timetable
         * we are currently editing. On other hand, changing effective (start) date can change the end date.
         */

        if (interactionGuard.begin()) return;

        // TODO: split the function into smaller ones?
        if (getMode() == Mode.VIEW) {
            // Locked, never called, unless fixDatePickerAlwaysEditableViaButton from initialize didn't help...
            logger.finer("End date picker still editable while in view mode");
        }
        else /* Mode.NEW or Mode.EDIT */ {
            final var newDate = endDatePicker.getValue().atStartOfDay(ZoneId.systemDefault());
            final var timetable = getTimetable();

            final var nextTimetable = getNextTimetable();
            if (nextTimetable != null) {
                if (!newDate.isAfter(timetable.getEffectiveDate())) {
                    // Reject the change
                    endDatePicker.setValue(nextTimetable.getEffectiveDate().toLocalDate());
                    interactionGuard.end();
                    return;
                }

                final var nextDate = nextTimetable.getEffectiveDate();
                if (newDate.isBefore(nextDate)) /* easy case */ {
                    // TODO: allow remember the choice (bc low invasive)?
                    ButtonBar.ButtonData choice = null;
                    if (nextTimetable.isEmpty()) {
                        choice = ButtonBar.ButtonData.YES;
                    }
                    if (choice == null) {
                        final var dialog = new Alert(Alert.AlertType.CONFIRMATION);
                        dialog.setTitle("Ustawienie daty końca");
                        dialog.setHeaderText(null);
                        dialog.setContentText(("Nowa data końca poprzedza datę efektywną następnego harmonogramu, " +
                                "więc po zmianie rozpocznie się on wczesnej. Czy chcesz kontynuować? \n\n" +
                                "Alternatywnie, może zostać utworzony pusty harmonogram w luce (od %s do %s).")
                                .formatted(newDate, nextDate));
                        dialog.getButtonTypes().setAll(
                                ButtonType.YES,
                                new ButtonType("Nowy pusty", ButtonBar.ButtonData.OTHER),
                                ButtonType.CANCEL);
                        choice = dialog.showAndWait().orElse(ButtonType.CLOSE).getButtonData();
                    }
                    switch (choice) {
                        case YES -> {
                            nextTimetable.setEffectiveDate(newDate);
                        }
                        case OTHER -> {
                            // Add new empty timetable right after this one
                            final var newTimetable = new Timetable(newDate);
                            timetables.add(currentTimetableIndex + 1, newTimetable);
                            // No need to reorder nor reselect: we already had next timetable and reset end date
                            // buttons active, so UI is okay. The index was not invalidated either.
                        }
                        default /* incl. cancel & close */ -> {
                            // Reject the change
                            endDatePicker.setValue(nextTimetable.getEffectiveDate().toLocalDate());
                        }
                    }
                }
                else /* extend-over-existing case */ {
                    /* Find first not maybe-to-be-skipped timetable */

                    // Iterate through more recent timetables, until we find the one after new date;
                    // collecting timetables to be skipped (removed) or edited (very last one).
                    boolean everyLastOne = false;
                    boolean anyNotEmpty = false;
                    final var passed = new ArrayList<Timetable>(10);
                    passed.add(nextTimetable);
                    for (int i = currentTimetableIndex + 2; i < timetables.size(); i++) {
                        var otherTimetable = timetables.get(i);
                        if (otherTimetable.getEffectiveDate().isAfter(newDate)) {
                            break;
                        }
                        passed.add(otherTimetable);
                        if (!otherTimetable.isEmpty()) {
                            anyNotEmpty = true;
                        }
                        if (i == timetables.size() - 1) {
                            everyLastOne = true;
                        }
                    }

                    var confirmed = !anyNotEmpty; // if every single is empty, confirm by default
                    if (!confirmed) {
                        if (passed.size() > 1) {
                            confirmed = requireConfirmation("Ustawienie daty końca",
                                    ("Nowa data końca przekracza datę efektywną innych wzorów harmonogramu, " +
                                     "co spowoduje ich nadpisaniem (zostaną utracone). Czy chcesz kontynuować?"),
                                    ButtonType.CANCEL);
                        }
                        else /* only one */ {
                            confirmed = requireConfirmation("Ustawienie daty końca",
                                    ("Nowa data końca przekracza datę efektywną kolejnego wzoru harmonogramu, " +
                                     "co spowoduje jej przesunięciem. Czy chcesz kontynuować?"),
                                    ButtonType.CANCEL);
                        }
                    }

                    if (confirmed) {
                        if (everyLastOne) {
                            // We passed every last one timetable, create new empty one to limit current one
                            timetables.removeAll(passed);
                            final var newTimetable = new Timetable(newDate);
                            timetables.add(currentTimetableIndex + 1, newTimetable);
                            // No need to reorder nor reselect: we already had next timetable and reset end date
                            // buttons active, so UI is okay. The index was not invalidated either.
                        }
                        else {
                            // Remove fully omitted timetables, and edit the very last one effective date
                            final var toBeEdited = passed.remove(passed.size() - 1);
                            toBeEdited.setEffectiveDate(newDate);
                            timetables.removeAll(passed);
                            // No reorder nor reselect is needed
                        }
                    }
                    else {
                        // Reject the change
                        endDatePicker.setValue(nextTimetable.getEffectiveDate().toLocalDate());
                    }
                }
            }
            else /* no next timetable */ {
                if (!newDate.isAfter(timetable.getEffectiveDate())) {
                    // Reject the change
                    endDatePicker.setValue(null);
                    interactionGuard.end();
                    return;
                }

                // Add new empty one without even asking
                final var newTimetable = new Timetable(newDate);
                timetables.add(currentTimetableIndex + 1, newTimetable);
                nextTimetableButton.setDisable(false); // no full select to update UI is needed I think
            }
        }

        interactionGuard.end();
    }

    @FXML
    protected void resetEndDateAction(ActionEvent actionEvent) {
        if (interactionGuard.begin()) return;

        final var nextTimetable = getNextTimetable();
        if (nextTimetable != null) {
            if (!nextTimetable.isEmpty()) {
                if (!requireConfirmation("Potwierdzenie przedłużenia poprzez usunięcie",
                        ("Po obecnym wzorze harmonogramu istnieje następny niepusty - efektywny od %s. " +
                         "Czy na pewno chcesz przedłużyć datę końca obecnego harmonogramu? " +
                         "Spowoduje to usunięcie następnego harmonogramu (w celu wydłużenia obecnego).")
                                .formatted(nextTimetable.getEffectiveDate()),
                        ButtonType.CANCEL)) {
                    interactionGuard.end();
                    return;
                }
            }

            timetables.remove(nextTimetable);
            select(currentTimetableIndex); // updates UI (incl. end date picker, reset end date and next table buttons)
        }

        interactionGuard.end();
    }

    @FXML
    protected void goScheduleAction(ActionEvent actionEvent) {
        interactionGuard.begin();

        // TODO: allow in edit if not dirty
        if (getMode() == Mode.EDIT) {
            final var dialog = new Alert(Alert.AlertType.WARNING);
            dialog.setTitle("Niezapisane zmiany");
            dialog.setHeaderText(null);
            dialog.setContentText("Musisz najpierw anulować lub zapisać zmiany.");
            dialog.showAndWait();
            return;
        }

        getParentController().goToView(
                MainWindowController.Views.SCHEDULE,
                getUser(),
                getTimetable().getEffectiveDate()
        );

        interactionGuard.end();
    }

    @FXML
    protected void addEntryAction(ActionEvent actionEvent) {
        if (interactionGuard.begin()) return;

        // TODO: warning about problems with editing past/already effective timetable,
        //  prefer adding new (only once in edit session)
        final var dialog = new TimetableEntryEditDialog(null, getTimetable());
        dialog.showAndWait();
        switch (dialog.getState()) {
            case NEW_COMMITTED, EDIT_COMMITTED, DELETE_COMMITTED -> {
                // TODO: change for weekPane.refresh() or something even more specific
                //  to avoid re-rendering/resetting all rows/columns etc.
                weekPane.setEntries(getTimetable().getEntries());
            }
        }

        interactionGuard.end();
    }

    @FXML
    protected void editEntryButton(ActionEvent actionEvent) {
        if (interactionGuard.begin()) return;

        // TODO: warning about problems with editing past/already effective timetable,
        //  prefer adding new (only once in edit session)
        // TODO: add selecting entries (focusable by tab too),
        //  open edit dialog for selected one, etc.
        // TODO: best thing would be having controls under the week pane instead separate dialog,
        //  allowing for previewing the changes; and allow user to drag to resize/move the entries,
        //  but it's advanced stuff and im not paid nor hyped for the project...

        interactionGuard.end();
    }

    @FXML
    protected void deleteTimetableAction(ActionEvent actionEvent) {
        if (interactionGuard.begin()) return;

        if (currentTimetableIndex == 0) {
            final var dialog = new Alert(Alert.AlertType.WARNING);
            dialog.setTitle("Nie można usunąć");
            dialog.setHeaderText(null);
            dialog.setContentText("Nie można usunąć pierwszego wzoru harmonogramu: Musi istnieć chociaż jeden.");
            dialog.showAndWait();
            interactionGuard.end();
            return;
        }

        final var timetable = getTimetable();
        final var nextTimetable = getNextTimetable();

        String reasons = "";
        if (!timetable.isEmpty()) {
            reasons += "Wybrany wzór harmonogramu jest niepusty, jego wpisy godzin zostaną usunięte. ";
        }
        // TODO: allow remember (if empty)
        reasons += "Usunięcie spowoduje też przedłużenie efektywności poprzedniego wzoru harmonogramu " +
                "do daty końca usuwanego (do %s). "
                        .formatted(nextTimetable != null ? nextTimetable.getEffectiveDate() : "zawsze");

        if (!requireConfirmation("Potwierdzenie usunięcia",
                reasons + "Czy chcesz kontynuować?", ButtonType.CANCEL)) {
            interactionGuard.end();
            return;
        }

        timetables.remove(timetable);

        final var count = timetables.size();
        if (currentTimetableIndex >= count) {
            select(count - 1); // select last
        }
        else {
            select(currentTimetableIndex);
        }

        interactionGuard.end();
    }

    @FXML
    protected void newTimetableAction(ActionEvent actionEvent) {
        if (interactionGuard.begin()) return;

        if (getMode() == Mode.VIEW) {
            setMode(Mode.EDIT);
        }

        final var lastTimetable = timetables.get(timetables.size() - 1);
        if (!lastTimetable.isEmpty()) {
            final var newTimetable = new Timetable(lastTimetable.getEffectiveDate().plusDays(7));
            timetables.add(newTimetable);
        }
        select(timetables.size() - 1);

        interactionGuard.end();
    }

    @FXML
    protected void editTimetableAction(ActionEvent actionEvent) {
        if (interactionGuard.begin()) return;

        // TODO: warning about problems with editing past/already effective timetable,
        //  prefer adding new (only once in edit session)
        setMode(Mode.EDIT);

        interactionGuard.end();
    }

    @FXML
    protected void cancelAction(ActionEvent actionEvent) {
        if (interactionGuard.begin()) return;

        // TODO: ask only when dirty
        // TODO: default no to avoid losing changes by accident
        if (requireConfirmation("Potwierdzenie anulowania edycji",
                "Czy na pewno chcesz anulować i stracić wprowadzone zmiany?", ButtonType.NO)) {
            cancelForce();
        }

        interactionGuard.end();
    }

    @FXML
    protected void saveAction(ActionEvent actionEvent) {
        if (interactionGuard.begin()) return;

        logger.fine("Saving...");

        // TODO: check if other user modified the timetables just before us?
        // TODO: lock UI (Mode.SAVING?) while saving?

        final var user = getUser();
        transaction(entityManager -> {
            final var toBeRemoved = new ArrayList<>(getUser().getTimetables());
            for (final var timetable : timetables) {
                if (timetable.getId() == null) {
                    logger.finer("Persisting (new) timetable: " + timetable);
                    timetable.setUser(user);
                    entityManager.persist(timetable);
                }
                else {
                    toBeRemoved.remove(timetable);
                    logger.finer("Merging (edited) timetable: " + timetable);
                    entityManager.merge(timetable); // results in selects before update XD
                    // TODO: solve by using Active pattern or DAO (custom entity operations, avoid stupid Hibernate)
                }
            }
            for (var timetable : toBeRemoved) {
                logger.finer("Removing (deleted) timetable: " + timetable);
                entityManager.remove(timetable);
            }
        });
        logger.fine("Saved");

        // TODO: toast?

        setMode(Mode.VIEW);

        interactionGuard.end();
    }
}
