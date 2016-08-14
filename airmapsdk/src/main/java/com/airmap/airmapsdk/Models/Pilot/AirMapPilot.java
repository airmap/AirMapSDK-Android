package com.airmap.airmapsdk.Models.Pilot;

import com.airmap.airmapsdk.Models.AirMapBaseModel;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vansh Gandhi on 6/15/16.
 * Copyright © 2016 AirMap, Inc. All rights reserved.
 */
@SuppressWarnings("unused")
public class AirMapPilot implements Serializable, AirMapBaseModel {
    private String pilotId;
    private String email;
    private String firstName;
    private String lastName;
    private String username;
    private String pictureUrl;
    private String phone;
    private AirMapPilotVerificationStatus verificationStatus;
    private AirMapPilotMetaData userMetaData;
    private AirMapPilotMetaData appMetaData;
    private AirMapPilotStats stats;

    public AirMapPilot(JSONObject profileJson) {
        constructFromJson(profileJson);
    }

    public AirMapPilot() {
    }

    @Override
    public AirMapPilot constructFromJson(JSONObject json) {
        if (json != null) {
            setPilotId(json.optString("id"));
            setEmail(json.optString("email"));
            setFirstName(json.optString("first_name"));
            setLastName(json.optString("last_name"));
            setPhone(json.optString("phone"));
            setPictureUrl(json.optString("picture_url"));
            setUsername(json.optString("username"));
            setVerificationStatus(new AirMapPilotVerificationStatus(json.optJSONObject("verification_status")));
            setUserMetaData(new AirMapPilotMetaData(json.optJSONObject("user_metadata")));
            setAppMetaData(new AirMapPilotMetaData(json.optJSONObject("app_metadata")));
            setStats(new AirMapPilotStats(json.optJSONObject("statistics")));
        }
        return this;
    }

    //Does not submit phone number
    public Map<String, String> getAsParams() {
        Map<String, String> params = new HashMap<>();
        params.put("first_name", getFirstName());
        params.put("last_name", getLastName());
        params.put("user_metadata", getUserMetaData().getAsParams());
        params.put("app_metadata", getAppMetaData().getAsParams());
        return params;
    }

    public String getEmail() {
        return email;
    }

    public AirMapPilot setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public AirMapPilot setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public AirMapPilot setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public String getPhone() {
        return phone;
    }

    public AirMapPilot setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public AirMapPilot setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
        return this;
    }

    public String getPilotId() {
        return pilotId;
    }

    public AirMapPilot setPilotId(String pilotId) {
        this.pilotId = pilotId;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public AirMapPilot setUsername(String username) {
        this.username = username;
        return this;
    }

    public AirMapPilotStats getStats() {
        return stats;
    }

    public AirMapPilot setStats(AirMapPilotStats stats) {
        this.stats = stats;
        return this;
    }

    public AirMapPilotVerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public AirMapPilot setVerificationStatus(AirMapPilotVerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
        return this;
    }

    public AirMapPilotMetaData getUserMetaData() {
        return userMetaData;
    }

    public AirMapPilot setUserMetaData(AirMapPilotMetaData userMetaData) {
        this.userMetaData = userMetaData;
        return this;
    }

    public AirMapPilotMetaData getAppMetaData() {
        return appMetaData;
    }

    public AirMapPilot setAppMetaData(AirMapPilotMetaData appMetaData) {
        this.appMetaData = appMetaData;
        return this;
    }

    /**
     * Comparison based on ID
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof AirMapPilot && getPilotId().equals(((AirMapPilot) o).getPilotId());
    }

    @Override
    public String toString() {
        return getFullName();
    }
}