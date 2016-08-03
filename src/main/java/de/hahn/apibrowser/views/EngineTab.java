package de.hahn.apibrowser.views;

import de.hahn.apibrowser.settings.Api;
import de.hahn.apibrowser.settings.Package;
import de.hahn.apibrowser.settings.Page;
import de.hahn.apibrowser.settings.Settings;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

public class EngineTab extends Tab {

    private static final Logger logger = LogManager.getLogger(EngineTab.class.getName());

    @FXML
    private WebView browser;
    @FXML
    private TextField searchField;
    @FXML
    private Button scrollToTop;

    private WebEngine engine;

    public EngineTab(Page page) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("engine_tab.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        engine = browser.getEngine();
        engine.setUserStyleSheetLocation(
                ClassLoader.getSystemResource("javadoc-stylesheet.css").toString());

        setTitle(page.getName(), page.getPackage().withDots());

        loadContent(page.getContent());

        addScrollTopListener();
        addListener();
        addLoadWorkerListener();
    }

    private void setTitle(String name, String classPackage) {
        Label className = new Label(name);
        className.setFont(Font.font("Verdana", FontWeight.LIGHT, 12));

        // the package should be displayed in italic
        Label packageName = new Label("(" + classPackage + ")");
        packageName.setFont(Font.font("Verdana", FontPosture.ITALIC, 10));

        VBox box = new VBox(2, className, packageName);
        box.setAlignment(Pos.BASELINE_LEFT);
        setGraphic(box);
    }

    private void loadContent(String content) {
        engine.loadContent(content);
    }

    private static final String EVENT_TYPE_CLICK = "click";

    private void addLoadWorkerListener() {
        engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                EventListener listener = ev -> {
                    String domEventType = ev.getType();
                    if (domEventType.equals(EVENT_TYPE_CLICK)) {
                        String href = ((Element) ev.getTarget()).getAttribute("href");
                        logger.trace("'clicked' link " + href);

                        if (href == null) {
                            return;
                        }

                        if (href.startsWith("https://")) {
                            setTitle("external link", "");
                            return;
                        }

                        Package clickedLink = new Package(href);

                        Collection<Api> apis = Settings.get().getApis();
                        Optional<Page> first = apis.stream().flatMap(api -> api.getPages().stream())
                                .filter(p -> p.getPackage().equals(clickedLink))
                                .findFirst();

                        if (first.isPresent()) {
                            Page page = first.get();
                            logger.trace("found local page " + page);
                            loadContent(page.getContent());
                            setTitle(page.getName(), page.getPackage().withDots());
                        } else {
                            logger.trace("no matching page found.");
                        }
                    }
                };

                Document doc = engine.getDocument();
                NodeList nodeList = doc.getElementsByTagName("a");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    ((EventTarget) nodeList.item(i))
                            .addEventListener(EVENT_TYPE_CLICK, listener, false);
                }
            }
        });
    }

    private void addClickListener() {

    }

    private void addScrollTopListener() {
        ImageView icon = new ImageView("arrow.png");
        icon.setFitHeight(16);
        icon.setFitWidth(16);
        scrollToTop.setGraphic(icon);
        scrollToTop.setOnMouseClicked(event -> {
            scrollToTop();
        });
    }

    private void addListener() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            clearSelection();
        });
        searchField.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                findNextString(searchField.getText());
                event.consume();
            }
        });
    }

    /**
     * Toggle the visibility of the search field.
     */
    public void toggleSearchField() {
        searchField.setVisible(!searchField.isVisible());
        searchField.requestFocus();
        if (!searchField.getText().isEmpty()) searchField.clear();
    }

    /**
     * Scroll the get page to the top
     */
    @FXML
    private void scrollToTop() {
        engine.executeScript("(function() {\n" +
                "window.scroll(0,0);\n" +
                "})()");
    }

    private void findNextString(String s) {
        engine.executeScript("(function () {\n" +
                "if (window.find) {\n" +
                "strFound=self.find(\"" + s + "\");\n" +
                "if(!strFound) {" +
                "window.getSelection().removeAllRanges();" +
                "strFound=self.find(\"" + s + "\");\n" +
                "}" +
                "}\n" +
                "})()");
    }

    private void clearSelection() {
        engine.executeScript("(function () {\n" +
                "if(window.getSelection()) {" +
                "window.getSelection().removeAllRanges();" +
                "}" +
                "})()");
    }
}
