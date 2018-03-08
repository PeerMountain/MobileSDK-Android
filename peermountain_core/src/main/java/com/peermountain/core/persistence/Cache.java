package com.peermountain.core.persistence;


import android.support.annotation.RestrictTo;

import com.peermountain.core.model.guarded.AppDocument;
import com.peermountain.core.model.guarded.Contact;
import com.peermountain.core.model.guarded.PeerMountainConfig;
import com.peermountain.core.model.guarded.PmAccessToken;
import com.peermountain.core.model.guarded.PmJob;
import com.peermountain.core.model.guarded.Profile;
import com.peermountain.core.model.guarded.PublicUser;

import java.util.ArrayList;
import java.util.HashSet;


/**
 * Created by Galeen on 28.9.2016 г..
 * Data holder for fast access
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class Cache {
    private static Cache instance;
    private String sessionToken = null;
    private PmAccessToken accessToken;
    private PublicUser publicUser;
    private Profile profile;
    private PeerMountainConfig config;
    private String pin;
    private long lastTimeLogin;
    private HashSet<Contact> contacts = null;
    private ArrayList<AppDocument> documents = null;
    private ArrayList<PmJob> jobs = null;

    private Cache() {
    }

    private Cache(PeerMountainConfig config) {
        this.config = config;
    }

    static Cache getInstance() {
        if (instance == null)
            instance = new Cache();
        return instance;
    }

    void clearCache() {
        if (instance != null) {
            instance = new Cache(instance.config);
        }
    }

    void clearPublicProfileCache() {
        if(instance==null) return;
        instance.publicUser = null;
        instance.accessToken = null;
    }


    PmAccessToken getAccessToken() {
        return accessToken;
    }

    void setAccessToken(PmAccessToken accessToken) {
        this.accessToken = accessToken;
    }

    PublicUser getPublicUser() {
        return publicUser;
    }

    void setPublicUser(PublicUser publicUser) {
        this.publicUser = publicUser;
    }

    public PeerMountainConfig getConfig() {
        return config;
    }

    public void setConfig(PeerMountainConfig config) {
        this.config = config;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public long getLastTimeActive() {
        return lastTimeLogin;
    }

    public void setLastTimeActive(long lastTimeLogin) {
        this.lastTimeLogin = lastTimeLogin;
    }

    public HashSet<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(HashSet<Contact> contacts) {
        this.contacts = contacts;
    }

    public ArrayList<AppDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(ArrayList<AppDocument> documents) {
        this.documents = documents;
    }

    public ArrayList<PmJob> getJobs() {
        return jobs;
    }

    public void setJobs(ArrayList<PmJob> jobs) {
        this.jobs = jobs;
    }
}


