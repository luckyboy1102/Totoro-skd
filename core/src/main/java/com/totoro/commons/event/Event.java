package com.totoro.commons.event;

/**
 * 总线事件
 * Created by Chen on 2014/11/10.
 */
public class Event {

    public int what;

    public int arg1;

    public Object obj;

    public static Event getEvent() {
        return new Event();
    }

    public static Event getEvent(int what) {
        Event event = getEvent();
        event.what = what;
        return event;
    }
}
