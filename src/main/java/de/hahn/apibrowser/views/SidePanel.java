package de.hahn.apibrowser.views;

import de.hahn.apibrowser.settings.Page;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.util.Collection;

public class SidePanel extends BorderPane {

    private final TextField searchField;

    public SidePanel(Collection<Page> pages) {

        FilterableListView pagesListView = new FilterableListView(
                FXCollections.observableArrayList(pages));

        // add to layout
        searchField = SearchField.create(pagesListView);
        searchField.setMinWidth(0);
        searchField.setPrefWidth(100);
        pagesListView.setPrefWidth(100);

        BorderPane.setMargin(searchField, new Insets(12, 12, 12, 12));

        setTop(searchField);
        setCenter(pagesListView);
    }

    public void focusTextField() {
        searchField.requestFocus();
        searchField.selectAll();
    }
}
