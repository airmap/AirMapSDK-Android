package com.airmap.airmapsdk.Models.Status.Properties;

import com.airmap.airmapsdk.Models.AirMapBaseModel;
import com.airmap.airmapsdk.Utils;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Vansh Gandhi on 7/27/16.
 * Copyright © 2016 AirMap, Inc. All rights reserved.
 */
@SuppressWarnings("unused")
public class AirMapWildfireProperties implements AirMapBaseModel, Serializable {

    private int size; //In acres
    private Date effectiveDate;

    public AirMapWildfireProperties(JSONObject wildfireJson) {
        constructFromJson(wildfireJson);
    }

    public AirMapWildfireProperties() {

    }

    @Override
    public AirMapWildfireProperties constructFromJson(JSONObject json) {
        if (json != null) {
            setSize(json.optInt("size"));
            setEffectiveDate(Utils.getDateFromIso8601String(json.optString("date_effective")));
        }
        return this;
    }

    public int getSize() {
        return size;
    }

    public AirMapWildfireProperties setSize(int size) {
        this.size = size;
        return this;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public AirMapWildfireProperties setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
        return this;
    }
}