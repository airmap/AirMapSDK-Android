package com.airmap.airmapsdk;

import com.airmap.airmapsdk.Models.Coordinate;
import com.airmap.airmapsdk.Networking.Callbacks.AirMapCallback;
import com.airmap.airmapsdk.Networking.Services.AirMap;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Vansh Gandhi on 7/25/16.
 * Copyright © 2016 AirMap, Inc. All rights reserved.
 */
@SuppressWarnings("unused")
public class Utils {
    public static final String REFRESH_TOKEN_KEY = "AIRMAP_SDK_REFRESH_TOKEN";

    /**
     * Converts pressure in millimeters of mercury (Hg) to hectoPascals (hPa)
     *
     * @param hg Pressure in mm of Hg
     * @return Pressing in hPa
     */
    public static Float hgToHpa(float hg) {
        return (float) (hg * 33.864);
    }

    public static double feetToMeters(double feet) {
        return feet * 0.3048;
    }

    public static double metersToFeet(double meters) {
        return meters * 3.2808;
    }

    public static String getIso8601StringFromDate(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return isoFormat.format(date);
    }

    /**
     * Formats a string into a @link{java.util Date} object
     * @param iso8601
     * @return
     */
    public static Date getDateFromIso8601String(String iso8601) {
        if (iso8601 == null) {
            return null;
        }
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return isoFormat.parse(iso8601);
        } catch (ParseException e) {
            AirMapLog.e("AirMap Utils", "Error parsing date: " + e.getMessage());
            e.printStackTrace();
            return new Date();
        }
    }

    public static boolean statusSuccessful(JSONObject object) {
        return object != null && object.optString("status").equalsIgnoreCase("success");
    }

    public static void error(AirMapCallback listener, Exception e) {
        if (e != null && listener != null) {
            if (e.getMessage().toLowerCase().startsWith("unable to resolve host")) {
                listener.onError(new AirMapException("No internet connection"));
            } else {
                listener.onError(new AirMapException(e.getMessage()));
            }
        }
    }

    public static void error(AirMapCallback listener, int code, JSONObject json) {
        if (listener != null) {
            listener.onError(new AirMapException(code, json));
        }
    }

    /**
     * @return Default duration presets when creating a flight
     */
    public static StringNumberPair[] getDurationPresets() {
        return new StringNumberPair[]{
                new StringNumberPair("5 min", 5 * 60 * 1000L), //5 mins in millis
                new StringNumberPair("10 min", 10 * 60 * 1000L),
                new StringNumberPair("15 min", 15 * 60 * 1000L),
                new StringNumberPair("30 min", 30 * 60 * 1000L),
                new StringNumberPair("45 min", 45 * 60 * 1000L),
                new StringNumberPair("1 hr", 60 * 60 * 1000L),
                new StringNumberPair("1.5 hrs", 90 * 60 * 1000L),
                new StringNumberPair("2 hrs", 120 * 60 * 1000L),
                new StringNumberPair("2.5 hrs", 150 * 60 * 1000L),
                new StringNumberPair("3 hrs", 180 * 60 * 1000L),
                new StringNumberPair("3.5 hrs", 210 * 60 * 1000L),
                new StringNumberPair("4 hrs", 240 * 60 * 1000L)
        };
    }

    /**
     * @return Default altitude presets when creating a flight
     */
    public static StringNumberPair[] getAltitudePresets() {
        return new StringNumberPair[]{
                new StringNumberPair("50 ft", feetToMeters(50)),
                new StringNumberPair("100 ft", feetToMeters(100)),
                new StringNumberPair("200 ft", feetToMeters(200)),
                new StringNumberPair("400 ft", feetToMeters(400)),
                new StringNumberPair("500 ft", feetToMeters(500))
        };
    }

    /**
     * @return Default radius presets when creating a flight
     */
    public static StringNumberPair[] getRadiusPresets() {
        return new StringNumberPair[]{
                new StringNumberPair("25 ft", feetToMeters(25)),
                new StringNumberPair("50 ft", feetToMeters(50)),
                new StringNumberPair("100 ft", feetToMeters(100)),
                new StringNumberPair("150 ft", feetToMeters(150)),
                new StringNumberPair("200 ft", feetToMeters(200)),
                new StringNumberPair("300 ft", feetToMeters(300)),
                new StringNumberPair("400 ft", feetToMeters(400)),
                new StringNumberPair("500 ft", feetToMeters(500)),
                new StringNumberPair("750 ft", feetToMeters(750)),
                new StringNumberPair("1000 ft", feetToMeters(1000)),
                new StringNumberPair("1200 ft", feetToMeters(1200)),
                new StringNumberPair("1300 ft", feetToMeters(1300)),
                new StringNumberPair("1400 ft", feetToMeters(1400)),
                new StringNumberPair("1500 ft", feetToMeters(1500)),
                new StringNumberPair("1600 ft", feetToMeters(1600)),
                new StringNumberPair("1700 ft", feetToMeters(1700)),
                new StringNumberPair("1800 ft", feetToMeters(1800)),
                new StringNumberPair("1900 ft", feetToMeters(1900)),
                new StringNumberPair("2000 ft", feetToMeters(2000)),
                new StringNumberPair("2100 ft", feetToMeters(2100)),
                new StringNumberPair("2200 ft", feetToMeters(2200)),
                new StringNumberPair("2300 ft", feetToMeters(2300)),
                new StringNumberPair("2400 ft", feetToMeters(2400)),
                new StringNumberPair("2500 ft", feetToMeters(2500)),
                new StringNumberPair("2600 ft", feetToMeters(2600)),
                new StringNumberPair("2700 ft", feetToMeters(2700)),
                new StringNumberPair("2800 ft", feetToMeters(2800)),
                new StringNumberPair("2900 ft", feetToMeters(2900)),
                new StringNumberPair("3000 ft", feetToMeters(3000))
        };
    }

    public static int indexOfMeterPreset(double meters, StringNumberPair[] pairs) {
        for (int i = 0; i < pairs.length; i++) {
            StringNumberPair pair = pairs[i];
            if (pair.value.doubleValue() == meters) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOfDurationPreset(long millis) {
        for (int i = 0; i < getDurationPresets().length; i++) {
            StringNumberPair pair = getDurationPresets()[i];
            if (pair.value.longValue() == millis) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Pair of String and a Number
     */
    public static class StringNumberPair {

        public StringNumberPair(String label, long value) {
            this.label = label;
            this.value = new BigDecimal(value);
        }

        public StringNumberPair(String label, double value) {
            this.label = label;
            this.value = new BigDecimal(value);
        }

        public String label;
        public BigDecimal value;

    }

    /**
     * Makes a polygon with many sides to simulate a circle
     *
     * @param radius     Radius of the circle to draw
     * @param coordinate Coordinate to draw the circle
     * @return A "circle"
     */
    public static PolygonOptions getCirclePolygon(double radius, Coordinate coordinate) {
        //We'll make a polygon with 45 sides to make a "circle"
        int degreesBetweenPoints = 8; //45 sides
        int numberOfPoints = (int) Math.floor(360 / degreesBetweenPoints);
        double distRadians = radius / 6371000.0; // earth radius in meters
        double centerLatRadians = coordinate.getLatitude() * Math.PI / 180;
        double centerLonRadians = coordinate.getLongitude() * Math.PI / 180;
        ArrayList<LatLng> points = new ArrayList<>(); //array to hold all the points
        for (int index = 0; index < numberOfPoints; index++) {
            double degrees = index * degreesBetweenPoints;
            double degreeRadians = degrees * Math.PI / 180;
            double pointLatRadians = Math.asin(Math.sin(centerLatRadians) * Math.cos(distRadians) + Math.cos(centerLatRadians) * Math.sin(distRadians) * Math.cos(degreeRadians));
            double pointLonRadians = centerLonRadians + Math.atan2(Math.sin(degreeRadians) * Math.sin(distRadians) * Math.cos(centerLatRadians),
                    Math.cos(distRadians) - Math.sin(centerLatRadians) * Math.sin(pointLatRadians));
            double pointLat = pointLatRadians * 180 / Math.PI;
            double pointLon = pointLonRadians * 180 / Math.PI;
            LatLng point = new LatLng(pointLat, pointLon);
            points.add(point);
        }
        return new PolygonOptions().addAll(points).strokeColor(0xA8B13232).fillColor(0xA8B13232);
    }

    public static String readInputStreamAsString(InputStream in) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(in);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while (result != -1) {
            byte b = (byte) result;
            buf.write(b);
            result = bis.read();
        }
        return buf.toString();
    }

    public static String getMapboxApiKey() {
        try {
            return AirMap.getConfig().getJSONObject("mapbox").getString("access_token");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error getting mapbox key from airmap.config.json");
        }
    }

    public static String getCallbackUrl() {
        try {
            JSONObject auth0 = AirMap.getConfig().getJSONObject("auth0");
            return auth0.getString("callback_url");
        } catch (JSONException e) {
            throw new RuntimeException("client_id and/or callback_url not found in airmap.config.json");
        }
    }

    public static String getClientId() {
        try {
            JSONObject auth0 = AirMap.getConfig().getJSONObject("auth0");
            return auth0.getString("client_id");
        } catch (JSONException e) {
            throw new RuntimeException("client_id and/or callback_url not found in airmap.config.json");
        }
    }

    public static String getDebugUrl() {
        try {
            return AirMap.getConfig().getJSONObject("internal").getString("debug_url");
        } catch (JSONException e) {
            return "v2/";
        }
    }

    public static String getMqttDebugUrl() {
        try {
            return AirMap.getConfig().getJSONObject("internal").getString("mqtt_url");
        } catch (JSONException e) {
            return "v2/";
        }
    }

    public static String getTelemetryDebugUrl() {
        try {
            return AirMap.getConfig().getJSONObject("internal").getString("telemetry_url");
        } catch (JSONException e) {
            return "v2/";
        }
    }
}