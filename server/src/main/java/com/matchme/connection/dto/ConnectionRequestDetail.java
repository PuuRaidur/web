package com.matchme.connection.dto;

import java.time.Instant;

public class ConnectionRequestDetail {
    public Long id;
    public Long senderId;
    public Instant createdAt;

    public ConnectionRequestDetail(Long id, Long senderId, Instant createdAt) {
        this.id = id;
        this.senderId = senderId;
        this.createdAt = createdAt;
    }
}
