package com.simpl.pay.sample.zc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.simpl.pay.sample.zc.utils.BaseApi;
import com.simpl.pay.sample.zc.utils.Intents;
import com.simpl.pay.sample.zc.utils.Prefs;
import com.simpl.pay.sample.zc.utils.Urls;
import com.simpl.pay.sample.zc.utils.Utils;

import org.json.JSONException;
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

public class UserActivity extends AppCompatActivity {
    public boolean hasZeroClickToken = false;
    // Add your merchant id here
    public String merchantId = "e4a905492fc1ec16d8f2d25bfd9885c7";

    @BindView(R.id.etPhoneNo)
    EditText etPhoneNo;

    @BindView(R.id.etEmail)
    EditText etEmail;

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.bProceed)
    public void onClickProceed() {
        if (etPhoneNo.getText().toString().length() > 0)
            if (etEmail.getText().toString().length() > 0) {
                RequestBody body = BaseApi.createRequestBody(Utils.hasTokenPayload(Long.parseLong(etPhoneNo.getText().toString())));
                Request hasZCTokenRequest = BaseApi.buildPOSTRequest(body, Urls.HAS_TOKEN, "");

                BaseApi.executeRequest(hasZCTokenRequest, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.v("SIMPLSDK", "" + e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()){
                            try {
                                JSONObject res = new JSONObject(response.body().string());
                                if(res.getBoolean("success"))
                                    hasZeroClickToken = true;

                                Intent cartIntent = Intents.getCartActivityIntent(context);
                                cartIntent.putExtra("phone_no", etPhoneNo.getText().toString());
                                cartIntent.putExtra("email", etEmail.getText().toString());
                                cartIntent.putExtra("has_token", hasZeroClickToken);
                                cartIntent.putExtra("merchant_id", merchantId);
                                startActivity(cartIntent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } else Toast.makeText(UserActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(UserActivity.this, "Enter phone number", Toast.LENGTH_SHORT).show();

    }

}
