package pl.edu.ur.pz.clinicapp.controls;

import javafx.scene.control.SingleSelectionModel;

/**
 * Class responsible for managing selection inside week pane.
 */
public class WeekPaneSelectionModel<T extends WeekPane.Entry> extends SingleSelectionModel<T> {
    final public WeekPane<T> weekPane;

    public WeekPaneSelectionModel(WeekPane<T> weekPane) {
        this.weekPane = weekPane;

        // TODO: shouldn't it be a weak listener?
        selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            var oldCell = weekPane.findEntryCell(oldValue);
            if (oldCell != null) {
                oldCell.updateSelected(false);
            }

            var newCell = weekPane.findEntryCell(newValue);
            if (newCell != null) {
                newCell.updateSelected(true);
            }
        });

        // TODO: (weak) listener to entries property of week pane:
        //  + on removed if hit currently selected -> deselect the node, clear selection
        //  + on added/removed -> do nothing? reselect to make sure index is synced properly
    }

    @Override
    protected T getModelItem(int index) {
        if (index == -1) return null;
        return weekPane.getEntries().get(index);
    }

    @Override
    protected int getItemCount() {
        return weekPane.getEntries().size();
    }

    public void selectCell(WeekPane.EntryCell<T> cell) {
        select(cell.getItem());
    }
}
