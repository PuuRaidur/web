package com.matchme.realtime;

public class PresenceEvent {
    public String type;
    public Long userId;
    public Boolean online;

    public PresenceEvent(Long userId, Boolean online) {
        this.type = "presence";
        this.userId = userId;
        this.online = online;
    }
}
