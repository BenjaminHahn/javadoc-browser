package de.hahn.apibrowser.eventbus;

import javafx.scene.input.KeyEvent;

public class DelegateKeyEvent {
    public final KeyEvent keyEvent;
    public final Object source;

    public DelegateKeyEvent(Object source, KeyEvent event) {
        this.keyEvent = event;
        this.source = source;
    }
}
