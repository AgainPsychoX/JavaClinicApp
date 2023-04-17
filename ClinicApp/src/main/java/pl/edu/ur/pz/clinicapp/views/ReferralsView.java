package pl.edu.ur.pz.clinicapp.views;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

public class ReferralsView extends ChildControllerBase<MainWindowController> {
    @FXML protected VBox vBox;
    @FXML protected TableView table;
    @FXML protected TableColumn patientCol;
    @FXML protected TableColumn specCol;
    @FXML protected TableColumn commentsCol;
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
            commentsCol.setPrefWidth(tableWidth * 0.2);
            dateCol.setPrefWidth(tableWidth * 0.2);
            specCol.setPrefWidth(tableWidth * 0.2);
            doctorCol.setPrefWidth(tableWidth * 0.2);
            commentsCol.setPrefWidth(commentsCol.getPrefWidth() + tableWidth - (patientCol.getPrefWidth() + dateCol.getPrefWidth() + doctorCol.getPrefWidth() + specCol.getPrefWidth()));
        });
    }

    @Override
    public void refresh() {

    }
}
