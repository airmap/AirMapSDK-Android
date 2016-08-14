package com.airmap.airmapsdk.Networking.Services;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Map.Entry;

import okhttp3.Callback;
import okhttp3.CertificatePinner;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Vansh Gandhi on 6/16/16.
 * Copyright © 2016 AirMap, Inc. All rights reserved.
 */
@SuppressWarnings("unused")
public class AirMapClient {

    private String authToken;
    private String xApiKey;

    private OkHttpClient client;

    /**
     * Initialize the client
     *
     * @param apiKey The API key
     * @param token  The Auth token
     */
    public AirMapClient(final String apiKey, final String token) {
        this.authToken = token;
        this.xApiKey = apiKey;
        clearAndResetHeaders(); //Will initialize OkHttpClient client
    }

    /**
     * Set the Auth Token
     *
     * @param token The updated token
     */
    public void setAuthToken(String token) {
        this.authToken = token;
        clearAndResetHeaders();
    }

    /**
     * Set API key
     *
     * @param apiKey The updated API key
     */
    public void setApiKey(String apiKey) {
        this.xApiKey = apiKey;
        clearAndResetHeaders();
    }

    /**
     * Make a GET call with params
     *
     * @param url      The full url to GET
     * @param params   The params to add to the request
     * @param callback An OkHttp Callback
     */
    public void get(String url, Map<String, String> params, Callback callback) {
        client.newCall(new Builder().url(urlBodyFromMap(url, params)).get().build()).enqueue(callback);
    }

    /**
     * Make a GET call
     *
     * @param url      The full url to GET
     * @param callback An OkHttp Callback
     */
    public void get(String url, Callback callback) {
        client.newCall(new Builder().url(url).get().build()).enqueue(callback);
    }

    /**
     * Make a POST call with params
     *
     * @param url      The full url to POST
     * @param params   The params to add to the request
     * @param callback An OkHttp Callback
     */
    public void post(String url, Map<String, String> params, Callback callback) {
        Request request = new Builder().url(url).post(bodyFromMap(params)).build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * Make a POST call with a JSON body
     *
     * @param url      The full url to POST
     * @param params   The JSON params to add to the request
     * @param callback An OkHttp Callback
     */
    public void postWithJsonBody(String url, JSONObject params, Callback callback) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, params.toString());
        Request request = new Builder().url(url).post(body).build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * Make a POST call with no params
     *
     * @param url      The full url to POST
     * @param callback An OkHttp Callback
     */
    public void post(String url, Callback callback) {
        post(url, null, callback);
    }

    /**
     * Make a PATCH call with params
     *
     * @param url      The full url to PATCH
     * @param params   The params to add to the request
     * @param callback An OkHttp Callback
     */
    public void patch(String url, Map<String, String> params, Callback callback) {
        Request request = new Builder().url(url).patch(bodyFromMap(params)).build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * Make a PUT call with params
     *
     * @param url      The full url to PUT
     * @param params   The params to add to the request
     * @param callback An OkHttp Callback
     */
    public void put(String url, Map<String, String> params, Callback callback) {
        Request request = new Builder().url(url).put(bodyFromMap(params)).build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * Make a PUT call
     *
     * @param url      The full url to PUT
     * @param callback An OkHttp Callback
     */
    public void put(String url, Callback callback) {
        put(url, null, callback);
    }

    /**
     * Make a DELETE call
     *
     * @param url      The full url to DELETE
     * @param callback An OkHttp Callback
     */
    public void delete(String url, Callback callback) {
        Request request = new Builder().url(url).delete().build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * Clears Interceptor Headers and adds Authorization + x-Api-Key
     */
    public void clearAndResetHeaders() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (AirMap.isCertificatePinningEnabled()) {
            builder.certificatePinner(getCertificatePinner());
        }
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Builder newRequest = chain.request().newBuilder();
                if (xApiKey != null && !xApiKey.isEmpty()) {
                    newRequest.addHeader("x-Api-Key", xApiKey);
                }
                if (authToken != null && !authToken.isEmpty()) {
                    newRequest.addHeader("Authorization", "Bearer " + authToken);
                }
                return chain.proceed(newRequest.build());
            }
        });
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    AirMap.showLogin();
                }
                return response;
            }
        });
        //TODO: Check for active connections before reassigning client
        client = builder.build();
    }

    /**
     * Builds and Returns a CertificatePinner for AirMap API calls
     *
     * @return CertificatePinner
     */
    private CertificatePinner getCertificatePinner() {
        String host = "api.airmap.com";
        return new CertificatePinner.Builder()
                .add(host, "sha256/CJlvFGiErgX6zPm0H+oO/TRbKOERdQOAYOs2nUlvIQ0=")
                .add(host, "sha256/8Rw90Ej3Ttt8RRkrg+WYDS9n7IS03bk5bjP/UXPtaY8=")
                .add(host, "sha256/Ko8tivDrEjiY90yGasP6ZpBU4jwXvHqVvQI0GS3GNdA=")
                .build();
    }

    /**
     * Creates url based on map of params
     *
     * @param base The base url
     * @param map  The parameters to add to the url
     * @return The url with parameters embedded
     */
    private HttpUrl urlBodyFromMap(String base, Map<String, String> map) {
        HttpUrl.Builder builder = HttpUrl.parse(base).newBuilder(base);
        for (Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue() != null) {
                builder.addEncodedQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    /**
     * Creates a Request Body from a map of params
     *
     * @param map The parameters to add to the body
     * @return The request body
     */
    private FormBody bodyFromMap(Map<String, String> map) {
        FormBody.Builder formBody = new FormBody.Builder();
        if (map != null) {
            for (final Map.Entry<String, String> entrySet : map.entrySet()) {
                if (entrySet.getValue() != null) {
                    formBody.add(entrySet.getKey(), entrySet.getValue());
                }
            }
        }
        return formBody.build();
    }
}