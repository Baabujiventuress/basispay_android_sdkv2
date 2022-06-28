package com.basispaypg;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;

public class PaymentGatewayPaymentActivity extends AppCompatActivity {
    ProgressBar pb;
    WebView webview;
//    AppCompatButton button_close;
    String paymentResponse;

    public PaymentGatewayPaymentActivity() {
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_payment);
        this.webview = findViewById(R.id.pgPaymentGatewayWebview);
        this.pb = findViewById(R.id.progressBar);
//        button_close = (AppCompatButton)this.findViewById(R.id.button_close);
        this.pb.setVisibility(0);
        String postPaymentRequestParams = this.getIntent().getStringExtra(PGConstants.POST_PARAMS);

        try {
            this.webview.setWebViewClient(new WebViewClient() {
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    pb.setVisibility(8);
                    Log.i("log", "onPageFinished : " + url);

                    /*runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            if(url.equals("https://staging-demo.basispay.in/response.php")){
                            if (url.contains("https://connect.basispay.in/ui/response?")){
                                String[] s = url.split("&");
                                if (s[1].equals("success=true")) {
                                    paymentResponse = "Success";
                                }
                                else
                                {
                                    paymentResponse = "Failure";
                                }
                                button_close.setVisibility(View.VISIBLE);
                            }
                            *//*else {
                                button_close.setVisibility(View.GONE);
                            }*//*

                        }
                    });*/

                }

                public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                    super.onPageStarted(view, url, facIcon);
                    pb.setVisibility(0);
                    Log.i("log", "onPageStarted : " + url);
                }
            });
            WebSettings webSettings = this.webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            this.webview.getSettings().setDomStorageEnabled(true);
            this.webview.addJavascriptInterface(new MyJavaScriptInterface((Activity)this), "Android");
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setDomStorageEnabled(true);
            this.webview.clearHistory();
            this.webview.clearCache(true);
            this.webview.setWebChromeClient(new WebChromeClient() {
                public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                    return super.onJsAlert(view, url, message, result);
                }
            });
//            String postUrl = "https://staging-connect.basispay.in/checkout";
            String postUrl = "https://connect.basispay.in/checkout";
            Log.d("postParamValues", postPaymentRequestParams);
            this.webview.postUrl(postUrl, postPaymentRequestParams.getBytes());
        } catch (Exception var7) {
            StringWriter sw = new StringWriter();
            var7.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Toast.makeText(this.getBaseContext(), exceptionAsString, 0).show();
        }

        /*button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra(PGConstants.PAYMENT_RESPONSE, paymentResponse);
                PaymentGatewayPaymentActivity.this.setResult(-1, data);
                PaymentGatewayPaymentActivity.this.finish();
            }
        });*/

    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == 0) {
            switch(keyCode) {
                case 4:
                    if (this.webview.canGoBack()) {
                        this.webview.goBack();
                    } else {
                        this.finish();
                    }

                    return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    public void onBackPressed() {
        if (this.webview.getVisibility() == 0) {
            if (this.webview.canGoBack()) {
                this.webview.goBack();
            }

        } else {
            super.onBackPressed();
        }
    }

    public class MyJavaScriptInterface {
        Activity mActivity;
        public MyJavaScriptInterface(Activity activity) {
            this.mActivity = activity;
        }
        @JavascriptInterface
        public void showHTML(String html, String url) {
            Log.i("log", "showHTML : " + url + " : " + html);
        }

        @JavascriptInterface
        public void paymentResponse(String jsonStringResponse) {
            try {
                Log.d("", "ResponseJson: " + jsonStringResponse);
                JSONObject pgResponse = new JSONObject();
                if (!TextUtils.isEmpty(jsonStringResponse) &&
                        jsonStringResponse.contains("transaction_id")) {
                    pgResponse.put("status", "success");
                    pgResponse.put("payment_response", jsonStringResponse);
                } else {
                    pgResponse.put("status", "failed");
                    pgResponse.put("error_message", "No payment response received !");
                }
                Intent paymentResponseCallBackIntent = new Intent();
                paymentResponseCallBackIntent.putExtra(PGConstants.PAYMENT_RESPONSE, pgResponse.toString());
                PaymentGatewayPaymentActivity.this.setResult(-1, paymentResponseCallBackIntent);
                PaymentGatewayPaymentActivity.this.finish();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
