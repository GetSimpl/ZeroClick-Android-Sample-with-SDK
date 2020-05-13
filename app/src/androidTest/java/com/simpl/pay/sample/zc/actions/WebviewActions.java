package com.simpl.pay.sample.zc.actions;

import com.simpl.pay.sample.zc.network.NetworkHelper;
import com.simpl.pay.sample.zc.utils.TestUtils;

import org.json.JSONException;

import java.io.IOException;

import androidx.test.espresso.web.assertion.WebViewAssertions;
import androidx.test.espresso.web.webdriver.DriverAtoms;
import androidx.test.espresso.web.webdriver.Locator;

import static androidx.test.espresso.web.model.Atoms.script;
import static androidx.test.espresso.web.sugar.Web.onWebView;
import static androidx.test.espresso.web.webdriver.DriverAtoms.findElement;
import static org.junit.Assert.assertEquals;

public class WebviewActions {
    private String verification_id = "";
    private String otp = "";

    public WebviewActions() {
        TestUtils.wait(7000);
    }

    public WebviewActions fetchVerificationId() {
    	TestUtils.wait(2000);
        verification_id = onWebView().perform(script("return document.URL.split('?')[1].split('&')[0].split('=')[1]"))
                .get().getValue().toString();
        return this;
    }

    public WebviewActions fillOTP() {
        try {
            otp = NetworkHelper.getOTPFromVerificationId(verification_id);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            onWebView().withElement(findElement(Locator.ID, "otp-input")).perform(DriverAtoms.webKeys(otp));
        }
        return this;
    }

    public WebviewActions verifyOtpFill() {
        String otp = onWebView().perform(script("return document.getElementById('otp-input').value")).get().getValue().toString();
        assertEquals(this.otp, otp);
        return this;
    }

    public WebviewActions clickVerifyNumber() {
    	onWebView().perform(script("document.getElementById('link-btn').dispatchEvent(new Event('click'))"));
    	return this;
	}


    public WebviewActions clickVerifyOTP() {
        onWebView().perform(script("document.getElementById('confirm-btn').dispatchEvent(new Event('click'))"));
        TestUtils.wait(5000);
        return this;
    }

    public void clickLinkAccount() {
        TestUtils.wait(5000);
        boolean placeOrderButtonStatus = (Boolean) onWebView().perform(script("return document.getElementById(\"submit-button\").disabled")).get().getValue();
        if (placeOrderButtonStatus) {
            onWebView().perform(script("var e = new Event('touchstart');" +
                    "document.getElementsByClassName('js-agree-button')[0].dispatchEvent(e);"));
        }

        TestUtils.wait(4000);
        onWebView().perform(script("var e = new Event('touchstart');" +
                "document.getElementById('submit-button').dispatchEvent(e);"));
    }

    public void closeWebView() {
        onWebView()
                .perform(script("let e = new Event('touchstart');" + "document.getElementsByClassName('close')[0].dispatchEvent(e);"))
                .perform(script("let e = new Event('touchstart');" +"document.getElementsByClassName('button button--white button--medium button--noshadow')[0].dispatchEvent(e)"));
    }
}
