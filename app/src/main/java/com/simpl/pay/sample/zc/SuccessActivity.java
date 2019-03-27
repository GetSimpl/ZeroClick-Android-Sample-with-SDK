package com.simpl.pay.sample.zc;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.simpl.pay.sample.zc.utils.Prefs;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SuccessActivity extends AppCompatActivity {
    @BindView(R.id.tvTransactionSuccess)
    TextView tvTransactionSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        tvTransactionSuccess.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.bOkay)
    void onButtonOkayClick() {
        finish();
    }
}