package pl.edu.ur.pz.clinicapp.views;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

public class NotificationsView extends ChildControllerBase<MainWindowController> {
    @FXML protected TextField searchTextField;
    @FXML protected TableView table;
    @FXML protected Button markUnreadButton;
    @FXML protected Button deleteButton;
    @FXML protected TableColumn dateCol;
    @FXML protected TableColumn categoryCol;
    @FXML protected TableColumn fromCol;
    @FXML protected TableColumn contentCol;
    @FXML protected VBox vBox;

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void populate(Object... context) {
        vBox.widthProperty().addListener((obs, oldVal, newVal) -> {
            double tableWidth = newVal.doubleValue() - 50;
            dateCol.setPrefWidth(tableWidth * 0.2);
            categoryCol.setPrefWidth(tableWidth * 0.2);
            fromCol.setPrefWidth(tableWidth * 0.2);
            contentCol.setPrefWidth(tableWidth * 0.2);
            contentCol.setPrefWidth(contentCol.getPrefWidth() + tableWidth - (dateCol.getPrefWidth() + categoryCol.getPrefWidth() + fromCol.getPrefWidth()));
        });
    }

    @Override
    public void refresh() {

    }
}
