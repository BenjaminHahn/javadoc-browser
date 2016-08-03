package de.hahn.apibrowser.eventbus;

import de.hahn.apibrowser.settings.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.InvalidParameterException;

public class LoadPageEvent {
    final public Page page;
    private static final Logger log = LogManager.getLogger(LoadPageEvent.class.getName());

    public LoadPageEvent(Page page) {
        if (page == null) {
            throw new InvalidParameterException("page must not be null on event fired");
        }
        this.page = page;
    }
}
