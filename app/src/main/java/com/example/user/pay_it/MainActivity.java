package com.example.user.pay_it;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;

import com.misfit.misfitlinksdk.MFLSession;
import com.misfit.misfitlinksdk.publish.MFLCallBack;
import com.misfit.misfitlinksdk.publish.MFLCommand;
import com.misfit.misfitlinksdk.publish.MFLError;
import com.misfit.misfitlinksdk.publish.MFLGestureCommandDelegate;
import com.misfit.misfitlinksdk.publish.MFLGestureType;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    TextView text;
    private static final String TAG = "RapidAPI";
    public static final String SERVER_URL = "http://expensive-warthog-g6rs.rapidapi.io/signup";
    Switch mSwitchEnable;
    private static PayPalConfiguration config = new PayPalConfiguration()

            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)

            .clientId("AfvcjFPOWyAOSOQYUjvWJBh8k1VkWAWOCjMyqbbqXKPH3FDQFU6dxurMc7HQQkvKIpW2XEfzdSJgSGZD");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MFLSession.build(this.getApplicationContext());
        Intent intent = new Intent(this, PayPalService.class);
        text = (TextView) findViewById(R.id.textView);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        mSwitchEnable = (Switch) findViewById(R.id.switch1);
        applyStyle(mSwitchEnable.getTextOn(), mSwitchEnable.getTextOff());

        DeviceMonitorDelegate delegate = new DeviceMonitorDelegate();
        MFLSession.sharedInstance().setGestureCommandDelegate(delegate);
        startService(intent);
        mSwitchEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch switchEnable = (Switch) v;
                if (switchEnable.isChecked()) {
                    MFLSession.sharedInstance().enable("100", "H0OQkPOLPLnQD5H7pBbc44K9YE8eWJOP", new MFLCallBack() {
                        @Override
                        public void onResponse(final Map<String, Map<MFLGestureType, MFLCommand>> commandMapping, final List<MFLCommand> supportedCommands, final MFLError error) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (error != null) {
                                        Log.e(TAG, error.getLocalizedMessage());
                                        mSwitchEnable.setChecked(false);
                                        return;
                                    }
                                    mSwitchEnable.setChecked(MFLSession.sharedInstance().isEnabled());
                                }
                            });

                        }
                    });
                } else {
                    MFLSession.sharedInstance().disable();
                }

            }
        });

    }

    public void applyStyle(CharSequence switchTxtOn, CharSequence switchTxtOff){

        Spannable styleText = new SpannableString(switchTxtOn);
        StyleSpan style = new StyleSpan(Typeface.BOLD);
        styleText.setSpan(style, 0, switchTxtOn.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        styleText.setSpan(new ForegroundColorSpan(Color.GREEN), 0, switchTxtOn.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        mSwitchEnable.setTextOn(styleText);

        styleText = new SpannableString(switchTxtOff);
        styleText.setSpan(style, 0, switchTxtOff.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        styleText.setSpan(new ForegroundColorSpan(Color.RED), 0, switchTxtOff.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        mSwitchEnable.setTextOff(styleText);

    }

    public class DeviceMonitorDelegate implements MFLGestureCommandDelegate {
        @Override
        public void performActionByCommand(MFLCommand command, String serialNumber) {

            Log.i("It worked ", "performActionByCommand " + " " + command.getName() + " " + serialNumber);
            // add your code here
            switch(command.getName()){
                case "pay": MainActivity.this.presentPaypal();break;
                case "2": MainActivity.this.changeText2();break;
                case "3": MainActivity.this.changeText3();break;
                case "4": MainActivity.this.changeText4();break;
            }
        }
    }
    public void changeText4(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(Double.toString(Double.parseDouble(text.getText().toString())+ 10));
            }
        });
    }
    public void changeText2(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(Double.toString(Double.parseDouble(text.getText().toString())+ 1));
            }
        });
    }
    public void changeText3(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(Double.toString(Double.parseDouble(text.getText().toString())+ 5));
            }
        });
    }
    public void Recipt(){
        Intent intent = new Intent(this , Recipt.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    public void presentPaypal() {

        // PAYMENT_INTENT_SALE will cause the payment to complete immediately.
        // Change PAYMENT_INTENT_SALE to
        //   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
        //   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
        //     later via calls from your server.
        PayPalPayment payment = new PayPalPayment(new BigDecimal(text.getText().toString()), "USD",  " " + "Hamburger",
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        Recipt();
        if (resultCode == Activity.RESULT_OK) {
            final PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Log.i("paymentExample", confirm.toJSONObject().toString());

                    final JSONObject jsonConfirm    = confirm.toJSONObject();
                    final JSONObject jsonResp       = jsonConfirm.getJSONObject("response");

                    new AsyncTask<Void, Void, Void>() {
                        OkHttpClient client = new OkHttpClient();

                        @Override
                        protected Void doInBackground(Void... params) {
                            try {

                                RequestBody formBody = null;
                                try {
                                    formBody = new FormBody.Builder()
                                            .add("created", jsonResp.getString("create_time"))
                                            .add("id", jsonResp.getString("id"))
                                            .add("intent", jsonResp.getString("intent"))
                                            .add("state", jsonResp.getString("state"))
                                            .add("type", jsonConfirm.getString("response_type"))
                                            .build();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Request request = new Request.Builder()
                                        .url(SERVER_URL)
                                        .post(formBody)
                                        .build();

                                Response response = client.newCall(request).execute();
                                if (!response.isSuccessful()) {
                                    throw new IOException("Unexpected Code: " + response);
                                } else {
                                    /// PAY IS DONE + RAPIDAPI

                                    Log.d(TAG, response.body().string());
                                }
                            } catch (IOException e) {
                                Log.d(TAG, e.toString(), e.getCause());
                            }
                            return null;
                        }
                    }.execute();
                    // TODO: send 'confirm' to your server for verification.
                    // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                    // for more details.

                } catch (JSONException e) {
                    Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                }
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("paymentExample", "The user canceled.");
        }
        else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }
    }
}




