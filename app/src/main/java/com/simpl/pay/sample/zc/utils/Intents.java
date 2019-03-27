package com.simpl.pay.sample.zc.utils;

import android.content.Context;
import android.content.Intent;

import com.simpl.pay.sample.zc.CartActivity;
import com.simpl.pay.sample.zc.PaymentActivity;
import com.simpl.pay.sample.zc.SuccessActivity;

public class Intents {

    public static Intent getCartActivityIntent(Context context) {
        return new Intent(context, CartActivity.class);
    }

    public static Intent getPaymentActivityIntent(Context context) {
        return new Intent(context, PaymentActivity.class);
    }

    public static Intent getSuccessActivityIntent(Context context) {
        return new Intent(context, SuccessActivity.class);
    }
}
