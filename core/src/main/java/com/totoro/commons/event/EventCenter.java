package com.totoro.commons.event;

import de.greenrobot.event.EventBus;
import in.srain.cube.app.lifecycle.LifeCycleComponentManager;

public class EventCenter {

    private static final EventBus instance = new EventBus();

    private EventCenter() {
    }

    public static SimpleEventHandler bindContainerAndHandler(Object container, SimpleEventHandler handler) {
        LifeCycleComponentManager.tryAddComponentToContainer(handler, container);
        return handler;
    }

    public static final EventBus getInstance() {
        return instance;
    }
}
