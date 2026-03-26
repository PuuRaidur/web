package com.matchme.connection.dto;

import java.util.List;

public class ConnectionsResponse {
    public List<Long> ids;

    public ConnectionsResponse(List<Long> ids) {
        this.ids = ids;
    }
}
