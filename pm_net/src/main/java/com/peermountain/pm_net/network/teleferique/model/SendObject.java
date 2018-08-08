package com.peermountain.pm_net.network.teleferique.model;

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

    public SendObject preparePublicPersonaAddress(){
        query = "query {  teleferic {    persona {     address pubkey   }  } }";
        return this;
    }

    public SendObject preparePersonaKey(String personaAddress){
        query = String.format("query{\n" +
                "                    persona(\n" +
                "                        address: \"%s\"\n" +
                "                    ){\n" +
                "                        pubkey\n" +
                "                    }\n" +
                "                }",personaAddress);
        return this;
    }

    public SendObject preparePublicPersonaKey(){
        query = "query {  teleferic {    persona {     pubkey    }  } }";
        return this;
    }

    public SendObject prepareTime(){
        query = "query {  teleferic {    signedTimestamp  } }";
        return this;
    }
}
