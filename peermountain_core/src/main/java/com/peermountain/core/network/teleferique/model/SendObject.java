package com.peermountain.core.network.teleferique.model;

/**
 * Created by Galeen on 3/15/2018.
 */

public class SendObject {
    private String query;
    private String variables;

    public String getQuery() {
        return query;
    }

    public SendObject setQuery(String queryJson) {
        this.query = queryJson;
        return this;
    }

    public String getVariables() {
        return variables;
    }

    public SendObject setVariables(String variablesJson) {
        this.variables = variablesJson;
        return this;
    }
}
