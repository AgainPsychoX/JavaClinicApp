package pl.edu.ur.pz.clinicapp.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

public class VisitsView extends ChildControllerBase<MainWindowController> {
    @FXML protected VBox vBox;
    @FXML protected TableView table;
    @FXML protected TableColumn specCol;
    @FXML protected TableColumn dateCol;
    @FXML protected TableColumn doctorCol;

    @FXML
    protected void searchEnterAction(ActionEvent event) {

    }

    @FXML
    protected void sortAction(ActionEvent event) {

    }

    @FXML
    protected void newAction(ActionEvent event) {

    }

    @FXML
    protected void rescheduleAction(ActionEvent event) {

    }

    @FXML
    protected void cancelAction(ActionEvent event) {

    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void populate(Object... context) {
        vBox.widthProperty().addListener((obs, oldVal, newVal) -> {
            double tableWidth = newVal.doubleValue() - 50;
            dateCol.setPrefWidth(tableWidth * 0.2);
            specCol.setPrefWidth(tableWidth * 0.2);
            doctorCol.setPrefWidth(tableWidth * 0.2);
            specCol.setPrefWidth(specCol.getPrefWidth() + tableWidth - (dateCol.getPrefWidth() + doctorCol.getPrefWidth()));
        });
    }

    @Override
    public void refresh() {

    }
}
