package com.peermountain.core.persistence;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;

import com.peermountain.core.model.PublicUser;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Galeen on 21.10.2016 г..
 */
class MyJsonParser {
    private static String getString(JsonReader reader) throws IOException {
        if (reader.peek() != JsonToken.NULL)
            return reader.nextString();
        else
            reader.skipValue();
        return null;
    }

    private static Date getDate(JsonReader reader, SimpleDateFormat dateFormat) throws IOException {
        if (reader.peek() != JsonToken.NULL)
            try {
                return dateFormat.parse(reader.nextString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        else
            reader.skipValue();
        return null;
    }

    private static Integer getInt(JsonReader reader) throws IOException {
        if (reader.peek() != JsonToken.NULL)
            return reader.nextInt();
        else
            reader.skipValue();
        return -1;
    }

    private static boolean getBoolean(JsonReader reader) throws IOException {
        if (reader.peek() != JsonToken.NULL)
            return reader.nextBoolean();
        else
            reader.skipValue();
        return false;
    }

    private static double getDouble(JsonReader reader) throws IOException {
        if (reader.peek() != JsonToken.NULL)
            return reader.nextDouble();
        else
            reader.skipValue();
        return -1;
    }


    static String writeLoginProvider(Context ctx, String token, boolean isFB) throws IOException {
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        writer.beginObject();
        writer.name(isFB ? "sessionToken" : "accessToken").value(token);
        writer.endObject();
        writer.close();
        return sw.toString();
    }

//    public static Login readLogin(String response) throws IOException {
//        if (response == null) return null;
//        GsonBuilder builder = new GsonBuilder().serializeNulls();
//        Gson gson = builder.create();
//        return gson.fromJson(response, Login.class);
//    }

    static PublicUser readPublicUser(String json) throws IOException {
//        Gson gson = new Gson();
//        return gson.fromJson(json, LiUser.class);
        if (json == null) return null;
        String name = null;
        PublicUser liUser = new PublicUser();
        JsonReader reader = new JsonReader(new StringReader(json));
        reader.beginObject();
        while (reader.hasNext()) {
            name = reader.nextName();
            switch (name) {
                case "emailAddress":
                    liUser.setEmail(getString(reader));
                    break;
                case "firstName":
                    liUser.setFirstname(getString(reader));
                    break;
                case "lastName":
                    liUser.setSurname(getString(reader));
                    break;
                case "id":
                    liUser.setLinked_in(getString(reader));
                    break;
                case "pictureUrl":
                    liUser.setPictureUrl(getString(reader));
                    break;
                case "publicProfileUrl":
                    liUser.setPublicProfileUrl(getString(reader));
                    break;
                case "positions":
                    readCompanies(liUser, reader);
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        reader.close();
        return liUser;
    }

    // TODO: 10/3/2017 update to populate the right fields
    static String writePublicUser(PublicUser liUser) throws IOException {
        StringWriter sw = new StringWriter();
        JsonWriter writer = new JsonWriter(sw);
        writer.beginObject();
        writer.name("emailAddress").value(liUser.getEmail());
        writer.name("firstName").value(liUser.getFirstname());
        writer.name("lastName").value(liUser.getSurname());
        writer.name("id").value(liUser.getLinked_in());
        writer.name("pictureUrl").value(liUser.getPictureUrl());
        writer.endObject();
        writer.close();
        return sw.toString();
    }

    private static void readCompanies(PublicUser liUser, JsonReader reader) throws IOException {
        reader.beginObject();
        String name = null;
        while (reader.hasNext()) {
            name = reader.nextName();
            switch (name) {
                case "values":
                    reader.beginArray();
                    boolean found = false;
                    while (reader.hasNext()) {
                        if (found)//for now just gets the 1st one
                            reader.skipValue();
                        else
                            found = readCompany(liUser, reader, name);
                    }
                    reader.endArray();
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
    }

    private static boolean readCompany(PublicUser liUser, JsonReader reader, String name) throws IOException {
        while (reader.hasNext()) {

            reader.beginObject();
            String[] company = new String[4];
            while (reader.hasNext()) {
                name = reader.nextName();
                switch (name) {
                    case "company":
                        if (reader.hasNext()) {
                            reader.beginObject();
                            while (reader.hasNext()) {
                                name = reader.nextName();
                                if (name.equals("name")) {
                                    company[0] = getString(reader);
                                } else
                                    reader.skipValue();
                            }
                            reader.endObject();
                        }
                        break;
                    case "title":
                        company[1] = getString(reader);
                        break;
                    case "companyStartDate":
                        company[2] = getString(reader);
                        break;
                    case "companyEndDate":
                        company[3] = getString(reader);
                        break;
                    default:
                        reader.skipValue();
                }
            }
            reader.endObject();
            liUser.setCompany(company[0]);
            liUser.setCompanyTitle(company[1]);
            liUser.setCompanyStartDate(company[2]);
            liUser.setCompanyEndDate(company[3]);
        }
        return true;
    }


}
