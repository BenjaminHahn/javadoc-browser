package de.hahn.apibrowser.views;

import de.hahn.apibrowser.eventbus.Bus;
import de.hahn.apibrowser.eventbus.DelegateKeyEvent;
import de.hahn.apibrowser.eventbus.LoadPageEvent;
import de.hahn.apibrowser.settings.Page;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.function.Predicate;


public class FilterableListView extends ListView<Page> {
    private static final Logger log = LogManager.getLogger(FilterableListView.class.getName());

    private FilteredList<Page> filteredList;

    public FilterableListView(ObservableList<Page> items) {
        super(items);
        filteredList = new FilteredList<>(items);
        setCellFactory(param -> new DisplayPackageListCell());
        addKeyListner();
        setItems(filteredList);
    }

    public void filterList(final String searchWord) {
        Predicate<Page> filter = getFilter(searchWord);
        setCellFactory(param -> new DisplayPackageListCell());
        filteredList.setPredicate(filter);
    }

    public Optional<Page> findIfExists(final String searchWord) {
        return filteredList.stream()
                .filter(p -> lowerName(p).equalsIgnoreCase(removeFix(searchWord)))
                .findFirst();
    }

    private Predicate<Page> getFilter(String wordWithFix) {
        String withoutFix = removeFix(wordWithFix).toLowerCase();

        switch (extractPrefix(wordWithFix)) {
            case STAR_PREFIX:
                log.trace("filter star prefix: " + withoutFix);
                return p -> lowerName(p).equals(withoutFix);
            case QUESTION_MARK_PREFIX:
                log.trace("filter questionmark prefix: " + withoutFix);
                return p -> lowerName(p).startsWith(withoutFix);
            case QUESTION_MARK_SUFFIX:
                log.trace("filter questionmark suffix: " + withoutFix);
                return p -> lowerName(p).endsWith(withoutFix);
            default:
                log.trace("filter no prefix: " + withoutFix);
                return p -> lowerName(p).contains(withoutFix);

        }
    }

    private String removeFix(String s) {
        String cleared = StringUtils.remove(s, SearchField.Prefix.QUESTION_MARK_PREFIX.value);
        cleared = StringUtils.remove(cleared, SearchField.Prefix.STAR_PREFIX.value);
        cleared = StringUtils.remove(cleared, SearchField.Prefix.QUESTION_MARK_SUFFIX.value);
        return cleared;
    }

    private SearchField.Prefix extractPrefix(String searchWord) {
        if (searchWord.startsWith(SearchField.Prefix.STAR_PREFIX.value)) {
            return SearchField.Prefix.STAR_PREFIX;
        } else if (searchWord.startsWith(SearchField.Prefix.QUESTION_MARK_PREFIX.value)) {
            return SearchField.Prefix.QUESTION_MARK_PREFIX;
        } else if (searchWord.endsWith(SearchField.Prefix.QUESTION_MARK_SUFFIX.value)) {
            return SearchField.Prefix.QUESTION_MARK_SUFFIX;
        } else {
            return SearchField.Prefix.NONE;
        }
    }

    private String lowerName(Page p) {
        return p.getName()
                .toLowerCase();
    }

    public void selectFirst() {
        requestFocus();
        getSelectionModel().select(0);
    }

    public void addKeyListner() {
        addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                Page page = getSelectionModel().getSelectedItem();
                if (page != null) {
                    Bus.get().post(new LoadPageEvent(page));
                    event.consume();
                }
            } else {
                Bus.get().post(new DelegateKeyEvent(this, event));
            }
        });
    }

    private class DisplayPackageListCell extends ListCell<Page> {
        @Override
        protected void updateItem(Page page, boolean empty) {
            super.updateItem(page, empty);
            if (!empty) {
                Label className = new Label(page.getName());
                className.setFont(Font.font("Verdana", FontWeight.LIGHT, 12));

                // the package should be displayed in italic
                Label packageName = new Label("(" + page.getPackage().withDots() + ")");
                packageName.setFont(Font.font("Verdana", FontPosture.ITALIC, 11));

                HBox box = new HBox(2, className, packageName);
                box.setAlignment(Pos.BASELINE_LEFT);
                setGraphic(box);

                // add a click listener to the listcell
                setOnMouseClicked(event -> {
                    Bus.get().post(new LoadPageEvent(page));
                });
            } else {
                setText(null);
                setGraphic(null);
            }
        }
    }
}
