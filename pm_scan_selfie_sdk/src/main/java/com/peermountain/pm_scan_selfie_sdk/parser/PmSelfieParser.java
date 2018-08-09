package com.peermountain.pm_scan_selfie_sdk.parser;

import android.util.JsonReader;

import com.peermountain.common.utils.Pm_JsonParser;
import com.peermountain.pm_scan_selfie_sdk.model.VerifySelfie;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by Galeen on 8/9/2018.
 */
public class PmSelfieParser extends Pm_JsonParser {

    public static VerifySelfie readLiveSelfieResponse(String json) throws IOException {
        if (json == null) return null;
        JsonReader reader = new JsonReader(new StringReader(json));
        String name = null;
        VerifySelfie verifySelfie = null;
        reader.beginObject();
        while (reader.hasNext()) {
            if (verifySelfie == null) verifySelfie = new VerifySelfie();
            name = reader.nextName();
            switch (name) {
                case "liveliness":
                    verifySelfie.setLiveliness(getBoolean(reader));
                    break;
                case "humanFace":
                    verifySelfie.setHumanFace(getBoolean(reader));
                    break;
                case "faceMatch":
                    verifySelfie.setFaceMatch(getBoolean(reader));
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        return verifySelfie;
    }

}
