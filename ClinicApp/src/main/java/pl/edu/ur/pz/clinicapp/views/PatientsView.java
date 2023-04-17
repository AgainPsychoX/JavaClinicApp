package pl.edu.ur.pz.clinicapp.views;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

public class PatientsView extends ChildControllerBase<MainWindowController> {
    @FXML protected VBox vBox;
    @FXML protected TableView table;
    @FXML protected TableColumn nameCol;
    @FXML protected TableColumn surnameCol;
    @FXML protected TableColumn peselCol;
    @FXML protected TableColumn phoneCol;
    @FXML protected TableColumn emailCol;
    @FXML protected TableColumn addressCol;


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
            peselCol.setPrefWidth(tableWidth * 0.2);
            phoneCol.setPrefWidth(tableWidth * 0.2);
            emailCol.setPrefWidth(tableWidth * 0.2);
            addressCol.setPrefWidth(tableWidth * 0.2);
            addressCol.setPrefWidth(nameCol.getPrefWidth() + tableWidth - (surnameCol.getPrefWidth() + peselCol.getPrefWidth() + phoneCol.getPrefWidth()) + emailCol.getPrefWidth());
        });
    }

    @Override
    public void refresh() {

    }
}
