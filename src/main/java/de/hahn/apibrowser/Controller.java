package de.hahn.apibrowser;

import com.google.common.eventbus.Subscribe;
import de.hahn.apibrowser.eventbus.Bus;
import de.hahn.apibrowser.eventbus.DelegateKeyEvent;
import de.hahn.apibrowser.eventbus.LoadPageEvent;
import de.hahn.apibrowser.settings.Api;
import de.hahn.apibrowser.settings.Settings;
import de.hahn.apibrowser.views.ApiAccordion;
import de.hahn.apibrowser.views.EngineTab;
import de.hahn.apibrowser.views.EngineView;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.textfield.CustomTextField;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

import static de.hahn.apibrowser.views.ShortcutHelper.pressedControlF;
import static de.hahn.apibrowser.views.ShortcutHelper.pressedControlF4;
import static de.hahn.apibrowser.views.ShortcutHelper.pressedControlNumber;
import static de.hahn.apibrowser.views.ShortcutHelper.pressedEscape;
import static de.hahn.apibrowser.views.ShortcutHelper.pressedFKey;

public class Controller implements Initializable {

    private static Logger logger = LogManager.getLogger(Controller.class.getName());

    @FXML
    private ApiAccordion apiAccordion;

    @FXML
    private EngineView engineView;

    @FXML
    private VBox mainWindow;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initShortCuts();
        Bus.get().register(this);

        createApis();
    }

    private void initShortCuts() {
        mainWindow.setOnKeyPressed(event -> {
            logger.trace("mainWindow keyevent: " + event);

            // switch accordion with F+number
            if (pressedFKey(event)) {
                switchAccordionPane(event.getCode().ordinal() - KeyCode.F1.ordinal());
                event.consume();
            }

            // close the selected Tab with ALT + F4
            if (pressedControlF4(event)) {
                engineView.closeCurrentTab();
                event.consume();
                return;
            }

            // move cursor to api searchfield
            if (pressedEscape(event)) {
                Node focusOwner = apiAccordion.getScene().getFocusOwner();

                if (focusOwner instanceof CustomTextField) {
                    engineView.focusCurrentTab();
                } else {
                    apiAccordion.focusSearchField();
                }
                event.consume();
                return;
            }

            // display searchfield
            if (pressedControlF(event)) {
                engineView.toggleSearchField();
                event.consume();
                return;
            }

            // switch tabs via CONTROL + NUMBER(1-9)
            if (pressedControlNumber(event)) {
                switchTab(event.getCode().ordinal() - KeyCode.DIGIT1.ordinal());
                event.consume();
                return;
            }

            event.consume();
        });
    }

    public void createApis() {
        Settings settings = Settings.loadFile();
        Collection<Api> apis = settings.getApis();

        apiAccordion.addApis(apis);
    }

    private void switchTab(int index) {
        // check if index exceeds available tabs
        if (engineView.getTabs().size() > index) {
            engineView.getSelectionModel().select(index);
        }
    }

    private void switchAccordionPane(int index) {
        ObservableList<TitledPane> panes = apiAccordion.getPanes();
        // check if index exceeds available panes
        if (panes.size() > index) {
            TitledPane titledPane = panes.get(index);
            apiAccordion.setExpandedPane(titledPane);
        }
    }

    @Subscribe
    public void keyEvent(DelegateKeyEvent event) {
        mainWindow.getOnKeyPressed().handle(event.keyEvent);
    }

    @Subscribe
    public void loadPage(LoadPageEvent event) {
        EngineTab tab = new EngineTab(event.page);
        engineView.getTabs().add(tab);
        engineView.getSelectionModel().select(tab);
    }
}
