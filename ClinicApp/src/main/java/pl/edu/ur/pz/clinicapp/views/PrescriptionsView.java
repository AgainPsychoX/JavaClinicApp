package pl.edu.ur.pz.clinicapp.views;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

public class PrescriptionsView extends ChildControllerBase<MainWindowController> {
    @FXML protected VBox vBox;
    @FXML protected TableView table;
    @FXML protected TableColumn patientCol;
    @FXML protected TableColumn contentCol;
    @FXML protected TableColumn dateCol;
    @FXML protected TableColumn doctorCol;

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void populate(Object... context) {
        vBox.widthProperty().addListener((obs, oldVal, newVal) -> {
            double tableWidth = newVal.doubleValue() - 50;
            patientCol.setPrefWidth(tableWidth * 0.2);
            contentCol.setPrefWidth(tableWidth * 0.2);
            dateCol.setPrefWidth(tableWidth * 0.2);
            doctorCol.setPrefWidth(tableWidth * 0.2);
            contentCol.setPrefWidth(contentCol.getPrefWidth() + tableWidth - (patientCol.getPrefWidth() + dateCol.getPrefWidth() + doctorCol.getPrefWidth()));
        });
    }

    @Override
    public void refresh() {

    }
}
