package de.hahn.apibrowser.views;

import de.hahn.apibrowser.eventbus.Bus;
import de.hahn.apibrowser.eventbus.LoadPageEvent;
import de.hahn.apibrowser.settings.Api;
import de.hahn.apibrowser.settings.Page;
import javafx.scene.control.Accordion;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;

import java.util.Collection;

/**
 * A Accordion that creates a {@link TitledPane} and a {@link SearchField} for every added Api.
 */
public class ApiAccordion extends Accordion {

    public void addApis(Collection<Api> apis) {
        apis.forEach(this::addApi);
    }

    public void addApi(Api api) {
        // the list displays the api content
        SidePanel panel = new SidePanel(api.getPages());
        TitledPane pane = new TitledPane(api.getName(), panel);
        getPanes().add(pane);
    }

    public void focusSearchField() {
        ((SidePanel) getExpandedPane().getContent()).focusTextField();
    }

    private void openSelectedPage(ListView<Page> listview) {
        Page selectedPage = listview.getSelectionModel().getSelectedItem();
        Bus.get().post(new LoadPageEvent(selectedPage));
    }


}
