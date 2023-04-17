package pl.edu.ur.pz.clinicapp.views;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import pl.edu.ur.pz.clinicapp.MainWindowController;
import pl.edu.ur.pz.clinicapp.utils.ChildControllerBase;

public class AccountDetailsView extends ChildControllerBase<MainWindowController> {
    @FXML protected VBox vBox;

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
