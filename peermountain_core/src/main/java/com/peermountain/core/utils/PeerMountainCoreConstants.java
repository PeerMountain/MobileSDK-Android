package com.peermountain.core.utils;


/**
 * Created by Galeen on 14.3.2017 г..
 * Constants related to peermountain_core
 */

public class PeerMountainCoreConstants {
    private static final String hostLI = "api.linkedin.com";
    //linked in
    public static final String GET_LI_PROFILE = "https://" + hostLI + "/v1/people/~:(id,first-name,last-name," +
            "maiden-name,email-address,headline,picture-url,site-standard-profile-request,api-standard-profile-request," +
            "public-profile-url,picture-urls,positions)";
    public static final String topCardUrlLI = "https://" + hostLI + "/v1/people/~:(first-name,last-name," +
            "public-profile-url)";
    public static final String shareUrlLI = "https://" + hostLI + "/v1/people/~/shares";

    public static final String FULL_DATE_FORMAT = "dd/MM/yyyy HH:mm:ssZ";
    public static final String CREATE_TRIP_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String TIME_FORMAT = "h'H'mm a";

    public static final String DEEP_LINK_USER_ID = "user_id";

    public static final String LOCAL_IMAGE_DIR = "/images";

    public static final int KEYWORDS_SHOW_COUNT = 20;
    public static final int KEYWORDS_SHOW_TO_VALIDATE_COUNT = 28;
    public static final int MIN_KEYWORDS_SAVE = 6;
}


