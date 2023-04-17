package pl.edu.ur.pz.clinicapp.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SortEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import pl.edu.ur.pz.clinicapp.ClinicApplication;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.models.User;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

public class ExaminationsView extends ChildControllerBase<MainWindowController> {
    @FXML protected Button addTestButton;
    @FXML protected VBox vBox;
    @FXML protected TableView table;
    @FXML protected TableColumn dateCol;
    @FXML protected TableColumn doctorCol;
    @FXML protected TableColumn specializationCol;


    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void populate(Object... context) {
        if(ClinicApplication.user.role == User.Role.DOCTOR){
            addTestButton.setVisible(true);
        }

        vBox.widthProperty().addListener((obs, oldVal, newVal) -> {
            double tableWidth = newVal.doubleValue() - 50; // ew. pasek do przewijana
            dateCol.setPrefWidth(tableWidth * 0.2);
            doctorCol.setPrefWidth(tableWidth * 0.2);
            specializationCol.setPrefWidth(tableWidth * 0.2);
            specializationCol.setPrefWidth(specializationCol.getPrefWidth() + tableWidth - (dateCol.getPrefWidth() + doctorCol.getPrefWidth()));
        });
    }

    @Override
    public void refresh() {

    }


    public void addTest(ActionEvent event) {
    }

    public void searchEnterAction(ActionEvent event) {
    }

    public void sortAction(SortEvent<TableView> tableViewSortEvent) {
    }

    public void rescheduleAction(ActionEvent event) {
    }

    public void examinationDone(ActionEvent event) {
    }
}
