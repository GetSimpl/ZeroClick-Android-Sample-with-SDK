package com.simpl.pay.sample.zc.network;

import android.util.Log;

import com.simpl.pay.sample.zc.api.BaseApi;
import com.simpl.pay.sample.zc.objects.User;
import com.simpl.pay.sample.zc.utils.Urls_Test;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

import okhttp3.Request;
import okhttp3.Response;

import static com.simpl.pay.sample.zc.api.BaseApi.executeRequest;

public class NetworkHelper {
    public static User createUser(String testName) {
        Request request;
        switch (testName) {
            case "approvedUserLinking":
            case "approvedUserSuccessfulZCTransaction":
                request = BaseApi.buildGETRequest(Urls_Test.APPROVEDUSER_WITHOTP
                        + "?phone_number=" + generatePhoneNo()
                        + "&integration_type=ZERO_CLICK"
                        + "&subscribe=true"
                        + "&global_credit_limit=1000000");
                break;
            case "approvedUserLimitSpend":
                request = BaseApi.buildGETRequest(Urls_Test.APPROVEDUSER_WITHOTP
                        + "?phone_number=" + generatePhoneNo()
                        + "&integration_type=ZERO_CLICK"
                        + "&subscribe=true"
                        + "&global_credit_limit=10000");
                break;
            case "approvedUserInsufficientCredit":
                request = BaseApi.buildGETRequest(Urls_Test.APPROVEDUSER_INSUFFICIENTCREDIT
                        + "?phone_number=" + generatePhoneNo()
                        + "&integration_type=ZERO_CLICK"
                        + "&subscribe=true");
                break;
            case "approvedUserPendingBill":
                Log.v("FROM TESTS", "APPROVED USER PENDING BILLS");
                request = BaseApi.buildGETRequest(Urls_Test.PENDINNG_DUES
                        + "?phone_number=" + generatePhoneNo()
                        + "&integration_type=ZERO_CLICK"
                        + "&subscribe=true"
                        + "&global_credit_limit=10000");
                break;
            case "nonExistingUserLienking":
                //Teardown user and use the same phone number. That way the user is deleted. Hence, he is the non existing user.
                try {
                    String phoneNo = generatePhoneNo();
                    Response response = executeRequest(BaseApi.buildGETRequest(Urls_Test.TEARDOWN_USER
                            + "?phone_number=" + phoneNo
                            + "&integration_type=ZERO_CLICK"
                            + "&subscribe=true"));
                    Log.d("Teardown", "Response: " + response.body().string());
                    User user = new User();
                    user.setPhoneNumber(phoneNo);
                    user.setEmailId("integration_test@simpl.com");
                    return user;
                } catch (IOException e) {
                    e.printStackTrace();
                    return new User();
                }
           default:
                //Teardown user and use the same phone number. That way the user is deleted. Hence, he is the non existing user.
                String phoneNo = generatePhoneNo();
                removeUser(phoneNo);
                User user = new User();
                user.setPhoneNumber(phoneNo);
                user.setEmailId("integration_test@simpl.com");
                return user;
        }
        try {
            return getUserData(executeRequest(request));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        return new User();
    }

    public static String getOTPFromVerificationId(String verification_id) throws IOException, JSONException {
        Request request = BaseApi.buildGETRequest(Urls_Test.OTP_BY_VERIFICATION_ID + verification_id);
        Response response = executeRequest(request);
        JSONObject responseObject = new JSONObject(response.body().string());
        JSONObject data = responseObject.getJSONObject("data");
        return data.getString("otp_code");
    }


    private static String generatePhoneNo() {
        Random rnd = new Random();
        int phoneNo = 100 + rnd.nextInt(99);
        Log.i("Test Data", "Random Phone No: 2121200" + phoneNo);
        return "2121200" + phoneNo;
    }

    private static User getUserData(Response response) throws IOException, JSONException {
        JSONObject responseObject = new JSONObject(response.body().string());

        JSONObject data = responseObject.getJSONObject("data");
        JSONObject userObject = data.getJSONObject("user");
        JSONObject merchantObject = data.getJSONObject("merchant");
        User user = new User();
        user.setPhoneNumber(userObject.getString("phone_number"));
        user.setEmailId(userObject.getString("email"));
        user.setMerchantId(merchantObject.getString("merchant_id"));
        user.setMerchantSecret(merchantObject.getString("merchant_secret"));
        return user;
    }

    public static void removeUser(String phoneNo) {
        try {
            executeRequest(BaseApi.buildGETRequest(Urls_Test.TEARDOWN_USER
                    + "?phone_number=" + phoneNo));
            executeRequest(BaseApi.buildGETRequest(Urls_Test.DELETE_USER_TOKEN + "/" + phoneNo));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
