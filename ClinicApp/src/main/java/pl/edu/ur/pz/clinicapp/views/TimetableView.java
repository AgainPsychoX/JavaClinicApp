package pl.edu.ur.pz.clinicapp.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.controls.WeekPane;
import pl.edu.ur.pz.clinicapp.models.Timetable;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

import java.net.URL;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.ResourceBundle;

public class TimetableView extends ChildControllerBase<MainWindowController> implements Initializable {
    @FXML private VBox content;

    @FXML private DatePicker datePicker;

    @FXML private Text headerText;

    @FXML private Button nextWeekButton;

    @FXML private Button previousWeekButton;

    @FXML private WeekPane<Timetable.Entry> weekPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        final var timetable = new Timetable() {{
            getEntries().addAll(Arrays.asList(
                    new Timetable.Entry(this, DayOfWeek.MONDAY, 9 * 60, 15 * 60),
                    new Timetable.Entry(this, DayOfWeek.TUESDAY, 9 * 60, 15 * 60),
                    new Timetable.Entry(this, DayOfWeek.WEDNESDAY, 9 * 60, 15 * 60),
                    new Timetable.Entry(this, DayOfWeek.THURSDAY, 9 * 60, 15 * 60),
                    new Timetable.Entry(this, DayOfWeek.FRIDAY, 9 * 60, 15 * 60)
            ));
        }};
        weekPane.getEntries().addAll(timetable.getEntries()); // TODO: replace with setEntries(set)?
    }

    @Override
    public void populate(Object... context) {
        // TODO: two modes: 1) view by user/doctor (default current); 2) edit/new
    }

    @FXML
    void datePickerAction(ActionEvent event) {

    }

    @FXML
    void goNextWeekAction(ActionEvent event) {

    }

    @FXML
    void goPreviousWeekAction(ActionEvent event) {

    }
}
