package org.utexas.surewalk.data;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

/**
 * Created by sam on 11/15/13.
 *
 * Object that is sent when a walk is requested
 */
@ParseClassName("WalkRequest")
public class WalkRequest extends ParseObject {

    private static final String KEY_UTEID = "uteid";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE_NUMBER = "phoneNumber";
    private static final String KEY_START_LOC = "startPoint";
    private static final String KEY_END_LOC = "endPoint";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_DEVICE = "device";
    private static final String KEY_USERID = "installationId";

    public WalkRequest() {
        //per parse's instructions
        this.put(KEY_DEVICE, "android");
        this.put(KEY_USERID, ParseInstallation.getCurrentInstallation().getInstallationId());
    }

    public void setUTEID(String uteid) {
        if (uteid == null)
            throw new IllegalArgumentException("Param may not be null.");
        this.put(KEY_UTEID, uteid);
    }

    public void setName(String name) {
        if (name == null)
            throw new IllegalArgumentException("Param may not be null.");
        this.put(KEY_NAME, name);
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null)
            throw new IllegalArgumentException("Param may not be null.");
        this.put(KEY_PHONE_NUMBER, phoneNumber);
    }

    public void setStartLocation(double lat, double lon) {
        ParseGeoPoint parsePoint = new ParseGeoPoint(lat, lon);
        this.put(KEY_START_LOC, parsePoint);
    }

    public void setEndLocation(double lat, double lon) {
        ParseGeoPoint parsePoint = new ParseGeoPoint(lat, lon);
        this.put(KEY_END_LOC, parsePoint);
    }

    public void setEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Param may not be null.");
        }
        this.put(KEY_EMAIL, email);
    }

    public void setMessage(String message) {
        this.put(KEY_MESSAGE, message);
    }

    public String getEID() {
        return this.getString(KEY_UTEID);
    }

    public String getName() {
        return this.getString(KEY_NAME);
    }

    public String getPhoneNumber() {
        return this.getString(KEY_PHONE_NUMBER);
    }

    public String getStartLocation() {
        ParseGeoPoint geoPoint = this.getParseGeoPoint(KEY_START_LOC);
        return String.format("%.5f", geoPoint.getLatitude()) + ", " + String.format("%.5f", geoPoint.getLongitude());
    }
    
    public String getEndLocation() {
        ParseGeoPoint geoPoint = this.getParseGeoPoint(KEY_END_LOC);
        return String.format("%.5f", geoPoint.getLatitude()) + ", " + String.format("%.5f", geoPoint.getLongitude());
    }

    public String getEmail() {
        return this.getString(KEY_EMAIL);
    }
}
