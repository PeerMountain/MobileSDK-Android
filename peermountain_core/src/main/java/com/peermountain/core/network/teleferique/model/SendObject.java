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

    public SendObject getPublicPersonaAddress(){
        query = "query {  teleferic {    persona {     address    }  } }";
        return this;
    }

    public SendObject getPersonaKey(String personaAddress){
        query = String.format("query{\n" +
                "                    persona(\n" +
                "                        address: \"%s\"\n" +
                "                    ){\n" +
                "                        pubkey\n" +
                "                    }\n" +
                "                }",personaAddress);
        return this;
    }

    public SendObject getPublicPersonaKey(){
        query = "query {  teleferic {    persona {     pubkey    }  } }";
        return this;
    }

    public SendObject getTime(){
        query = "query {  teleferic {    signedTimestamp  } }";
        return this;
    }
}
