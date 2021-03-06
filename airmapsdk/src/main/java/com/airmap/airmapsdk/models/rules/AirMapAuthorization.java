package com.airmap.airmapsdk.models.rules;

import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.airmap.airmapsdk.models.AirMapBaseModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import static com.airmap.airmapsdk.util.Utils.optString;

public class AirMapAuthorization implements AirMapBaseModel, Serializable {

    public enum Status {
        NOT_REQUESTED, REJECTED_UPON_SUBMISSION, AUTHORIZED_PENDING_SUBMISSION, MANUAL_AUTHORIZATION, ACCEPTED, REJECTED, PENDING, CANCELLED;

        public static Status fromText(String text) {
            switch (text) {
                case "not_requested":
                    return NOT_REQUESTED;
                case "rejected_upon_submission":
                    return REJECTED_UPON_SUBMISSION;
                case "authorized_upon_submission":
                    return AUTHORIZED_PENDING_SUBMISSION;
                case "manual_authorization":
                    return MANUAL_AUTHORIZATION;
                case "pending":
                    return PENDING;
                case "accepted":
                    return ACCEPTED;
                case "rejected":
                    return REJECTED;
                case "cancelled":
                    return CANCELLED;
            }

            return REJECTED;
        }
    }

    private Status status;
    @Nullable
    private AirMapAuthority authority;
    private String description;
    private String message;
    private String referenceNumber;

    public AirMapAuthorization(JSONObject jsonObject) {
        constructFromJson(jsonObject);
    }

    public AirMapAuthorization() {

    }

    @Override
    public AirMapBaseModel constructFromJson(JSONObject json) {
        if (json.has("authority")) {
            setAuthority(new AirMapAuthority(json.optJSONObject("authority")));
        }
        setStatus(Status.fromText(optString(json, "status")));
        setMessage(optString(json, "message"));
        setDescription(optString(json, "description"));
        setReferenceNumber(optString(json, "reference_number"));
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Nullable
    public AirMapAuthority getAuthority() {
        return authority;
    }

    public void setAuthority(AirMapAuthority authority) {
        this.authority = authority;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject authorizationObject = new JSONObject();

        if (!TextUtils.isEmpty(getDescription())) {
            authorizationObject.put("description", getDescription());
        }

        String statusString = null;
        switch (getStatus()) {
            case NOT_REQUESTED:
                statusString = "not_requested";
                break;
            case REJECTED_UPON_SUBMISSION:
                statusString = "rejected_upon_submission";
                break;
            case AUTHORIZED_PENDING_SUBMISSION:
                statusString = "authorized_upon_submission";
                break;
            case MANUAL_AUTHORIZATION:
                statusString = "manual_authorization";
                break;
            case PENDING:
                statusString = "pending";
                break;
            case ACCEPTED:
                statusString = "accepted";
                break;
            case REJECTED:
                statusString = "rejected";
                break;
            case CANCELLED:
                statusString = "cancelled";
                break;
        }
        if (!TextUtils.isEmpty(statusString)) {
            authorizationObject.put("status", statusString);
        }

        if (!TextUtils.isEmpty(getMessage())) {
            authorizationObject.put("message", getMessage());
        }

        if (!TextUtils.isEmpty(getReferenceNumber())) {
            authorizationObject.put("reference_number", getReferenceNumber());
        }

        if (getAuthority() != null) {
            JSONObject authorityObject = new JSONObject();
            if (!TextUtils.isEmpty(getAuthority().getId())) {
                authorityObject.put("id", authority.getId());
            }

            if (!TextUtils.isEmpty(getAuthority().getName())) {
                authorityObject.put("name", authority.getName());
            }

            authorizationObject.put("authority", authorityObject);
        }

        return authorizationObject;
    }
}
