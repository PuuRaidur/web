package com.matchme.connection.dto;

public class ConnectionDetail {
    public Long connectionId;
    public Long otherUserId;

    public ConnectionDetail(Long connectionId, Long otherUserId) {
        this.connectionId = connectionId;
        this.otherUserId = otherUserId;
    }
}
