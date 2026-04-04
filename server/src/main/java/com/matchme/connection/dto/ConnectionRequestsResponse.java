package com.matchme.connection.dto;

import java.util.List;

// Response wrapper for incoming connection request ids.
public class ConnectionRequestsResponse {
    public List<Long> ids;

    public ConnectionRequestsResponse(List<Long> ids) {
        this.ids = ids;
    }
}
