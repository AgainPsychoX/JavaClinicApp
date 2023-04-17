package pl.edu.ur.pz.clinicapp.views;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

public class AccountsView extends ChildControllerBase<MainWindowController> {
    @FXML protected VBox vBox;
    @FXML protected TableView table;
    @FXML protected TableColumn nameCol;
    @FXML protected TableColumn surnameCol;
    @FXML protected TableColumn emailCol;
    @FXML protected TableColumn specCol;
    @FXML protected TableColumn phoneCol;

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void populate(Object... context) {
        vBox.widthProperty().addListener((obs, oldVal, newVal) -> {
            double tableWidth = newVal.doubleValue() - 50;
            nameCol.setPrefWidth(tableWidth * 0.2);
            surnameCol.setPrefWidth(tableWidth * 0.2);
            emailCol.setPrefWidth(tableWidth * 0.2);
            phoneCol.setPrefWidth(tableWidth * 0.2);
            specCol.setPrefWidth(tableWidth * 0.2);
            specCol.setPrefWidth(specCol.getPrefWidth() + tableWidth - (nameCol.getPrefWidth() + surnameCol.getPrefWidth() + emailCol.getPrefWidth() + phoneCol.getPrefWidth()));
        });
    }

    @Override
    public void refresh() {

    }
}
