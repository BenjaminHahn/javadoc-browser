package de.hahn.apibrowser.views;


import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;

public class EngineView extends TabPane {

    public void focusCurrentTab() {
        int selectedIndex = getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) { // no tab selected
            return;
        }

        StackPane stackPane = (StackPane) getTabs().get(selectedIndex).getContent(); //engine_tab.fxml
        stackPane.getChildren().get(0).requestFocus(); //webview
    }

    public void closeCurrentTab() {
        int selectedIndex = getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            getTabs().remove(selectedIndex);
        }
    }

    public void toggleSearchField() {
        EngineTab tab = (EngineTab) getSelectionModel().getSelectedItem();
        if (tab != null) tab.toggleSearchField();
    }

}
