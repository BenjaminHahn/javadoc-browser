package de.hahn.apibrowser.views;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class ShortcutHelper {

    public static boolean pressedEscape(KeyEvent event) {
        return event.getCode() == KeyCode.ESCAPE;
    }

    public static boolean pressedControlF4(KeyEvent event) {
        return event.isControlDown() && event.getCode() == KeyCode.F4;
    }

    public static boolean pressedControlF(KeyEvent event) {
        return event.isControlDown() && event.getCode() == KeyCode.F;
    }

    public static boolean pressedControlNumber(KeyEvent event) {
        return event.isControlDown() && event.getCode().isDigitKey();
    }

    public static boolean pressedFKey(KeyEvent event) {
        return event.getCode().isFunctionKey();
    }
}
