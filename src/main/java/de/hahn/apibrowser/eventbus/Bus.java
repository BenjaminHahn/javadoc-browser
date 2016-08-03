package de.hahn.apibrowser.eventbus;

import com.google.common.eventbus.EventBus;

public final class Bus {

    private static EventBus bus;

    static {
        bus = new EventBus("default");

    }

    public static EventBus get() {
        return bus;
    }
}
