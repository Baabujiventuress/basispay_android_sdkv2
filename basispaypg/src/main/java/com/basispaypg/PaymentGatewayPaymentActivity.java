package com.basispaypg;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.io.PrintWriter;
import java.io.StringWriter;

public class PaymentGatewayPaymentActivity extends AppCompatActivity {
    ProgressBar pb;
    WebView webview;

    public PaymentGatewayPaymentActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_payment);
        this.webview = (WebView)this.findViewById(R.id.pgPaymentGatewayWebview);
        this.pb = (ProgressBar)this.findViewById(R.id.progressBar);
        this.pb.setVisibility(0);
        String postPaymentRequestParams = this.getIntent().getStringExtra(PGConstants.POST_PARAMS);

        try {
            this.webview.setWebViewClient(new WebViewClient() {
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    pb.setVisibility(8);
                    Log.i("log", "onPageFinished : " + url);
                }

                public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                    super.onPageStarted(view, url, facIcon);
                    pb.setVisibility(0);
                    Log.i("log", "onPageFinished : " + url);
                }
            });
            WebSettings webSettings = this.webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            this.webview.addJavascriptInterface(new MyJavaScriptInterface(this), "Android");
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setDomStorageEnabled(true);
            this.webview.setWebChromeClient(new WebChromeClient() {
                public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                    return super.onJsAlert(view, url, message, result);
                }
            });


             String postUrl = "https://connect.basispay.in/checkout?" + postPaymentRequestParams;
             String postParamValues =  postPaymentRequestParams;
            Log.d("postParamValues",postParamValues);

            this.webview.postUrl(postUrl, (postParamValues).getBytes());
        } catch (Exception var7) {
            StringWriter sw = new StringWriter();
            var7.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Toast.makeText(this.getBaseContext(), exceptionAsString, 0).show();
        }

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
        Context mContext;

        MyJavaScriptInterface(Context c) {
            this.mContext = c;
        }

        @JavascriptInterface
        public void showHTML(String html, String url) {
            Log.i("log", "showHTML : " + url + " : " + html);
        }

        @JavascriptInterface
        public void paymentResponse(String jsonStringResponse) {
            try {
                Log.d("", "ResponseJson: " + jsonStringResponse);
                if (!jsonStringResponse.equals("null") && !jsonStringResponse.isEmpty() && jsonStringResponse.contains("transaction_id")) {
                    Intent data = new Intent();
                    data.putExtra(PGConstants.PAYMENT_RESPONSE, jsonStringResponse);
                    PaymentGatewayPaymentActivity.this.setResult(-1, data);
                    PaymentGatewayPaymentActivity.this.finish();
                }
            } catch (Exception var3) {
                var3.printStackTrace();
            }

        }
    }
}
