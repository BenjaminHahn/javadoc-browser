package de.hahn.apibrowser.eventbus;

import javafx.scene.input.KeyEvent;

public class DelegateToController {
    public final KeyEvent keyEvent;
    public final Object source;

    public DelegateToController(Object source, KeyEvent event) {
        this.keyEvent = event;
        this.source = source;
    }
}
