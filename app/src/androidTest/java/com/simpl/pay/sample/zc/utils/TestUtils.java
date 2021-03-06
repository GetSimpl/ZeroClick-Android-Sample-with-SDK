package com.simpl.pay.sample.zc.utils;

import com.simpl.pay.sample.zc.api.BaseApi;
import com.simpl.pay.sample.zc.api.ChargeTransactionAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TestUtils {

    public static final String REGEX_IPADDRESS = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    public static final String REGEX_UP_TIME = "^[0-9]*ms$";
    public static final String REGEX_UUID = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";
    public static final String REGEX_DISPLAY_RES = "^[0-9]{3,4}x[0-9]{3,4}x[0-9]{3}$";
    public static final String REGEX_BOOLEAN = "^(true|false)$";
    public static final String REGEX_MEMORY = "^[0-9]+\\D*$";
    public static final String REGEX_INTEGER = "^[0-9]*$";
    public static final String REGEX_FONTSIZE = "^[0-9]+([.][0-9]*)?$";
    public static final String REGEX_BATTERY = "^[0-9]*.[0-9]{0,2}$";
    public static final String REGEX_CSV = "^(([a-zA-Z0-9.]*)[,])*$";
    public static final String REGEX_LOCATION = "^(\\-?\\d+(\\.\\d+)?),\\s*(\\-?\\d+(\\.\\d+)?)$";

    public static void wait(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
