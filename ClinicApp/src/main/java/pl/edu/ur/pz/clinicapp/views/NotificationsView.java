package pl.edu.ur.pz.clinicapp.views;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

public class NotificationsView extends ChildControllerBase<MainWindowController> {
    @FXML protected TextField searchTextField;
    @FXML protected TableView table;
    @FXML protected Button markUnreadButton;
    @FXML protected Button deleteButton;

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void populate(Object... context) {

    }

    @Override
    public void refresh() {

    }
}
