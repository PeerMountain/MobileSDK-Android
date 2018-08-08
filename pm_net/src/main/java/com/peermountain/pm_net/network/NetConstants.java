package com.peermountain.pm_net.network;


import com.peermountain.common.CommonLibConfig;

/**
 * Created by Galeen on 18.1.2016 г..
 * constants related to network requests
 */

public class NetConstants {
    // TODO: 1/24/2018 update from config file
    public static final String SERVER_ADDRESS = "https://code-hospitality-dev.herokuapp.com/";
    public static final String SERVER_IMAGE = SERVER_ADDRESS + "resources/images/";
    public static final boolean SHOULD_USE_SSL = false;
    public static final String HEADER_LOCALE_FIELD = "User-Locale";
    public static final String HEADER_LOCALE = "EN";
    public static final boolean SHOULD_ADD_LOCALE = false;
    public static final String HEADER_AUTHORIZATION = "x-auth-token";
    public static final boolean DEBUG = CommonLibConfig.isDebug();

    public static final String API_USERS = "v1.0/users";
}
