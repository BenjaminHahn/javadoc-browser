package de.hahn.apibrowser.views;

import de.hahn.apibrowser.eventbus.Bus;
import de.hahn.apibrowser.eventbus.DelegateKeyEvent;
import de.hahn.apibrowser.eventbus.LoadPageEvent;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.textfield.TextFields;

/**
 * Class that creates a clearable {@link TextField} that filters a {@link ListView}
 */
public class SearchField {
    private static final Logger log = LogManager.getLogger(SearchField.class.getName());

    enum Prefix {
        NONE(""), STAR_PREFIX("*"), QUESTION_MARK_PREFIX("?"), QUESTION_MARK_SUFFIX("?");

        public String value = "";

        Prefix(String value) {
            this.value = value;
        }
    }

    private FilterableListView filterableListView;
    private TextField clearableTextField;

    public static TextField create(FilterableListView listView) {
        SearchField searchField = new SearchField();
        searchField.filterableListView = listView;
        searchField.clearableTextField = TextFields.createClearableTextField();

        searchField.addFilterListener();
        searchField.addKeyListener();
        return searchField.clearableTextField;
    }

    private SearchField addFilterListener() {
        clearableTextField.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    filterableListView.filterList(newValue);
                });
        return this;
    }

    private void addKeyListener() {
        // on ENTER open all matching pages
        clearableTextField.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                filterableListView.findIfExists(clearableTextField.getText())
                        .ifPresent(p -> Bus.get().post(new LoadPageEvent(p)));
                event.consume();
                return;
            } else if (event.getCode() == KeyCode.DOWN) {
                filterableListView.selectFirst();
            }
            // propagete the rest to interested components.
            if (ShortcutHelper.pressedFKey(event)) {
                Bus.get().post(new DelegateKeyEvent(this, event));
            }
        });
    }
}
