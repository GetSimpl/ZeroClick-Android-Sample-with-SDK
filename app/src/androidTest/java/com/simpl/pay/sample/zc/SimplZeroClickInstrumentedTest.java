package com.simpl.pay.sample.zc;

import android.util.Log;

import com.simpl.pay.sample.zc.actions.CartActions;
import com.simpl.pay.sample.zc.actions.PaymentActions;
import com.simpl.pay.sample.zc.actions.SuccessActions;
import com.simpl.pay.sample.zc.actions.UserActions;
import com.simpl.pay.sample.zc.actions.WebviewActions;
import com.simpl.pay.sample.zc.network.NetworkHelper;
import com.simpl.pay.sample.zc.objects.User;
import com.simpl.pay.sample.zc.utils.Status;
import com.simpl.pay.sample.zc.utils.TestUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SimplZeroClickInstrumentedTest {
    private User user = null;
    private String zeroClickToken = "";

    @Rule
    public ActivityTestRule<UserActivity> mUserActivityRule = new ActivityTestRule<>(UserActivity.class);

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setup() throws Exception {
        user = NetworkHelper.createUser(testName.getMethodName());
        Log.d("User", "Phone No:" + user.getPhoneNumber());
    }

    @Test
    public void approvedUserLinking() {
        new UserActions()
                .fillUserData(user)
                .proceedToCart();

        new  CartActions()
                .adjustCartItems(15000)
                .proceedToPayment();

        new PaymentActions()
                .checkApprovalSuccess()
                .clickSimplButton();

        new WebviewActions()
                .fetchVerificationId()
                .fillOTP()
                .verifyOtpFill()
                .clickVerifyOTP()
                .clickLinkAccount();

        new SuccessActions().clickOk();
    }

    @Test
    public void approvedUserSuccessfulZCTransaction() {
        approvedUserLinking();

        new UserActions()
                .proceedToCart();

        new CartActions()
                .proceedToPayment();

        new PaymentActions()
                .checkStatus(Status.ELIGIBILITY_SUCCESS)
                .checkSimplButtonText("Pay with Simpl")
                .clickSimplButton();

        new SuccessActions().clickOk();
    }

    @Test
    public void approvedUserLimitSpend() {
       approvedUserLinking();

        new UserActions()
                .proceedToCart();

        new CartActions()
                .adjustCartItems(15000)
                .proceedToPayment();

        new PaymentActions()
                .checkStatus(Status.NOT_ENOUGHT_CREDIT);
    }

    @Test
    public void approvedUserInsufficientCredit() {

        new UserActions()
                .fillUserData(user)
                .proceedToCart();

        new CartActions()
                .adjustCartItems(70000)
                .proceedToPayment();

        new PaymentActions()
                .checkApprovalSuccess()
                .clickSimplButton();

        new WebviewActions()
                .fetchVerificationId()
                .fillOTP()
                .verifyOtpFill()
                .clickVerifyOTP()
                .clickLinkAccount();

        TestUtils.wait(2000);
        new WebviewActions()
                .closeWebView();

        TestUtils.wait(2000);
        new PaymentActions()
                .checkStatus(Status.NOT_ENOUGHT_CREDIT);
    }

    @Test
    public void nonExistingUserLinking() {
        new UserActions()
                .fillUserData(user)
                .proceedToCart();

        new CartActions()
                .proceedToPayment();

        new PaymentActions()
                .checkApprovalFail();
    }

    @Test
    public void approvedUserPendingBill() {
        new UserActions()
                .fillUserData(user)
                .proceedToCart();

        new  CartActions()
                .adjustCartItems(10000)
                .proceedToPayment();

        new PaymentActions()
                .checkApprovalSuccess()
                .clickSimplButton();

        new WebviewActions()
                .fetchVerificationId()
                .fillOTP()
                .verifyOtpFill()
                .clickVerifyOTP()
                .clickLinkAccount();

        TestUtils.wait(2000);
        new WebviewActions()
                .closeWebView();

        TestUtils.wait(2000);
        new PaymentActions()
                .checkStatus(Status.PENDING_BILL);
    }

//
//    public void blockedUserLinking() {
//        //TODO cannot test this
//        new UserActions()
//                .fillUserData(user)
//                .proceedToCart();
//
//        new CartActions()
//                .proceedToPayment();
//
//        new PaymentActions()
//                .checkApprovalFail();
//    }

    @After
    public void teardown() throws Exception {
        //teardown user
        try {
            NetworkHelper.removeUser(user.getPhoneNumber());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("TEAR DOWN", "Tear down complete..");
    }
}