package com.simpl.pay.sample.zc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.simpl.android.fingerprint.SimplFingerprint;
import com.simpl.android.zeroClickSdk.Simpl;
import com.simpl.android.zeroClickSdk.SimplPaymentDueListener;
import com.simpl.android.zeroClickSdk.SimplUser;
import com.simpl.android.zeroClickSdk.SimplUserApprovalListenerV2;
import com.simpl.android.zeroClickSdk.SimplUserApprovalRequest;
import com.simpl.android.zeroClickSdk.SimplZeroClickTokenAuthorization;
import com.simpl.android.zeroClickSdk.SimplZeroClickTokenListener;

import com.simpl.pay.sample.zc.utils.BaseApi;
import com.simpl.pay.sample.zc.utils.Prefs;
import com.simpl.pay.sample.zc.utils.Urls;
import com.simpl.pay.sample.zc.utils.Utils;

import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PaymentActivity extends AppCompatActivity {
    public String merchantId = "";
    private String email = "";
    private String phoneNo = "";
    String zeroClickToken = "";
    boolean hasZeroClickToken;
    private int amount = 0;

    private Context mContext = this;


    @BindView(R.id.tvAmount)
    TextView tvAmount;

    @BindView(R.id.tvStatus)
    TextView tvStatus;

    @BindView(R.id.tvSimplPay)
    TextView tvSimplPay;

    @BindView(R.id.llSimplPay)
    LinearLayout llSimplPay;

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        fetchData();
        updateUI();
        initSimpl();

    }

    private void initSimpl() {
        if (initZeroClickSDK())
            if (!hasZeroClickToken) {
                callApproval();
            } else
                callEligibility();
        else Toast.makeText(this, "Simpl SDK initialization failed", Toast.LENGTH_SHORT).show();
    }

    private void fetchData() {
        email = getIntent().getStringExtra("email");
        phoneNo = getIntent().getStringExtra("phone_no");
        hasZeroClickToken = getIntent().getBooleanExtra("has_token", false);
        amount = getIntent().getIntExtra("amount", 0);
        merchantId = getIntent().getStringExtra("merchant_id");
    }

    private void updateUI() {
        tvAmount.setText(String.format("Rs. %d", amount / 100));
    }


    private boolean initZeroClickSDK() {
        try {
            // initialize Simpl SDK.
            if (merchantId == null || merchantId.equalsIgnoreCase(""))
                Simpl.init(this);
            else
                Simpl.init(this, merchantId);
            // make sure to remove this line, when you move to production
            Simpl.getInstance().runInSandboxMode();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    void callApproval() {
        // set up a new user with email and phone number
        SimplUser user = new SimplUser(email, phoneNo);

        //create an approval request with available user data
        SimplUserApprovalRequest approvalRequest = Simpl.getInstance().isUserApproved(user);

        // add the transaction amount as a param
        if (Prefs.getBooleanValue(context, Prefs.AMOUNT_IN_APPROVAL))
            approvalRequest.addParam("transaction_amount_in_paise", String.valueOf(amount));


        //add additional params to get the approval state of the user.
        approvalRequest.addParam("member_since", "2016");
        approvalRequest.addParam("user_location", "18.9750,72.8258");

        // request for approval call
        approvalRequest.execute(new SimplUserApprovalListenerV2() {
            @Override
            public void onSuccess(final boolean status, final String buttonText, final boolean showSimplIntroduction) {
                if (status) {
                    // User is approved.
                    // enable Simpl Button here
                    llSimplPay.setVisibility(View.VISIBLE);
                    tvStatus.setText("Status: Approval Call successful.");
                    tvSimplPay.setText("Link with simpl");
                } else {
                    if (showSimplIntroduction) {
                        // new user
                        llSimplPay.setVisibility(View.VISIBLE);
                        tvSimplPay.setText(buttonText);
                        tvStatus.setText("Status: Introduce Simpl");
                        tvStatus.setTextColor(getColor(R.color.primary));
                    } else {
                        //user not approved
                        llSimplPay.setVisibility(View.GONE);
                        tvStatus.setText("Status: Approval Call Failed");
                        tvStatus.setTextColor(getColor(R.color.colorRed));
                    }
                }
            }

            @Override
            public void onError(final Throwable throwable) {
                // User approval process returned an error.
                tvStatus.setText("Status: An error occured during the Approval Call");
                tvStatus.setTextColor(Color.parseColor("#aa0000"));
                Log.e("ZeroClickSDK", "isUserApproved() -> onError(): " + throwable.getMessage());
            }
        });
    }

    private void callEligibility() {
        // Initialize finegerprint
        // Generate fingerprint and send it with eligibility call
        SimplFingerprint.init(PaymentActivity.this, phoneNo, email);
        SimplFingerprint.getInstance().generateFingerprint((payload) -> {
            Log.v(PaymentActivity.class.getName(), "" + payload);

            RequestBody body = BaseApi.createRequestBody(Utils.getEligibilityPayload(Long.parseLong(phoneNo), amount, payload));
            Request transsactionRequest = BaseApi.buildPOSTRequest(body, Urls.ELIGIBILITY_CHECK, zeroClickToken);

            BaseApi.executeRequest(transsactionRequest, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject responseObject = new JSONObject(response.body().string());
                            if (responseObject.getBoolean("success")) {
                                runOnUiThread(() -> {
                                    llSimplPay.setVisibility(View.VISIBLE);
                                    tvStatus.setText("Status: You have enough balance to make this transaction");
                                    tvSimplPay.setText("Pay with Simpl");
                                });
                            } else {
                                // If eligibility call fails
                                onEligibilityFail(responseObject);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.v("Error", "Eligibility did not succeed");
                    }

                }

                @Override
                public void onFailure(Call call, IOException e) {
                    // Error occurred. Handle gracefully.

                }

            });
        });
    }

    @OnClick(R.id.llSimplPay)
    public void onClickSimplPay() {
        Log.d("ZeroClickSDK", "Simpl Pay button clicked.");
        if (!hasZeroClickToken) {
            Log.i("ZeroClickSDK", zeroClickToken);
            generateZCToken(email, phoneNo);
        } else {
            performZCTransaction();
        }
    }

    private void generateZCToken(String email, String phoneNo) {
        SimplZeroClickTokenListener tokenListener = new SimplZeroClickTokenListener() {
            @Override
            public void onSuccess(SimplZeroClickTokenAuthorization simplZeroClickTokenAuthorization) {
                // use the token generated to charge the transaction and store it for further transactions.
                Log.i("ZeroClickSDK", "Response: " + simplZeroClickTokenAuthorization.toString());

                //store zero click token
                zeroClickToken = simplZeroClickTokenAuthorization.getZeroClickToken();
                Prefs.setStringValue(context, Prefs.ZERO_CLICK_TOKEN, zeroClickToken);

                //call ZC transaction
                performZCTransaction();
            }

            @Override
            public void onFailure(Throwable throwable) {
                // transaction failed. Show the message in
                Log.e("ZeroClickSDK", "Simpl ZC Linking failed. Message: " + throwable.getMessage());
            }
        };

        //generate ZeroClickToken
        Simpl.getInstance().generateZeroClickToken(new SimplUser(email, phoneNo), tokenListener);
    }

    private void performZCTransaction() {
        // Use the saved transaction token to charge.
        Log.v("ZeroClickSDK", "Transacting using merchant APIs.");
        RequestBody body = BaseApi.createRequestBody(Utils.getChargePayload(Long.parseLong(phoneNo), amount));
        Request chargeRequest = BaseApi.buildPOSTRequest(body, Urls.PLACE_SIMPL_ORDER, zeroClickToken);

        try {
            BaseApi.executeRequest(chargeRequest, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (response.isSuccessful()) {
                            JSONObject res = new JSONObject(response.body().string());
                            Log.i("JSON RESPONSE PAYMENT", res + "");
                            if (res.getBoolean("success")) {
                                startActivity(new Intent(mContext, SuccessActivity.class));
                                finish();
                            } else {
                                // The error responses are same for both eligibility and charge call
                                onEligibilityFail(res);
                            }
                        }
                    } catch (Exception exception) {
                        // Handle Exception
                        Log.e("SIMPLSDK", "" + exception);
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("SimplZeroClickSDK", "" + e.getMessage());
                }

            });
        } catch (Exception e) {
            Log.e("ZeroClickSDK", "Error: " + e);
        }
    }

    private void onEligibilityFail (JSONObject responseObject) {
        try {
            // If the Eligibility returns false, navigate to redirection URL.
            // This will allow user to pay his outstanding dues and on success callback, you can initiate the charge call on server.
			if (responseObject.getString("error_code").equals("zero_click_token_not_found"))
				generateZCToken(email, phoneNo);
            else if (responseObject.getString("redirect_url") != null && !responseObject.getString("redirect_url").equals("null"))
                Simpl.getInstance().openRedirectionURL(PaymentActivity.this, responseObject.getString("redirect_url"),
                        new SimplUser(email, phoneNo)).execute(new SimplPaymentDueListener() {
                    @Override
                    public void onSuccess(String s) {
                        Log.v("ZeroClickSDK", "Redirect success " + s);
                        performZCTransaction();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        // Error occurred. Handle gracefully.

                    }
                });

            //update the UI.
            String error_code = responseObject.getString("error_code");
            updateStatus(error_code);
        }catch (Exception exception) {
            Log.e("SIMPLSDK", "" + exception);
            updateStatus("");
        }

    }

    private void updateStatus (String code){
        switch (code) {
            case "pending_dues":
                tvStatus.setText("Status: You have a pending bill");
                tvStatus.setTextColor(getColor(R.color.colorRed));
                break;
            case "unable_to_process":
                tvStatus.setText("Status: Transaction amount is greater than your credit limit");
                tvStatus.setTextColor(getColor(R.color.colorRed));
                break;
            case "user_unauthorized":
                tvStatus.setText("Status: You have been blocked on simpl");
                tvStatus.setTextColor(getColor(R.color.colorRed));
                // As the user is blocked you can also delete user zeroClickToken in database
                break;
            default:
                tvStatus.setText("Status: You don't have enough credit for this transaction");
                tvStatus.setTextColor(getColor(R.color.colorRed));
                break;
        }
    }

}
