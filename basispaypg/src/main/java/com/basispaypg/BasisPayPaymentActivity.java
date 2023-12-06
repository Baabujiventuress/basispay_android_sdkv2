package com.basispaypg;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class BasisPayPaymentActivity extends AppCompatActivity {
    ProgressBar pb;
    WebView webview;
    String referenceNo,success;

    public BasisPayPaymentActivity() {
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_payment);
        this.webview = findViewById(R.id.pgPaymentGatewayWebview);
        this.pb = findViewById(R.id.progressBar);
        this.pb.setVisibility(View.VISIBLE);
        String postPaymentRequestParams = this.getIntent().getStringExtra(BasisPayPGConstants.POST_PARAMS);
        String returnUrl = this.getIntent().getStringExtra(BasisPayPGConstants.PAYMENT_RETURN_URL);
        boolean isProduction = this.getIntent().getBooleanExtra(BasisPayPGConstants.IS_PRODUCTION,false);

        try {
            this.webview.setWebViewClient(new WebViewClient() {
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    pb.setVisibility(View.GONE);
                    Log.i("log", "onPageFinished : " + url);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String checkUrl;
                            String appUrl;
                            if (isProduction) { //TODO LIVE MODE
                                checkUrl = BasisPayPGConstants.PRODUCTION_URL+BasisPayPGConstants.CONTAIN_CHECK;
                                appUrl = BasisPayPGConstants.PRODUCTION_URL+BasisPayPGConstants.CONTAIN_RES;
                            } else { //TODO TEST MODE
                                checkUrl = BasisPayPGConstants.STAGING_URL+BasisPayPGConstants.CONTAIN_CHECK;
                                appUrl = BasisPayPGConstants.STAGING_URL+BasisPayPGConstants.CONTAIN_RES;
                            }
                            if (url.contains(checkUrl)){
                                String[] s = url.split(appUrl);
                                Map<String, String> mapValue = getQueryMap(s[1]);

                                referenceNo = mapValue.get("?ref");
                                success = mapValue.get("success");
                            }

                        }
                    });

                }

                public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                    super.onPageStarted(view, url, facIcon);
                    pb.setVisibility(View.VISIBLE);
                    Log.i("log", "onPageStarted : " + url);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject pgResponse = new JSONObject();
                            if (url.equalsIgnoreCase(returnUrl)){
                                try {
                                    pgResponse.put("referenceNo", referenceNo);
                                    pgResponse.put("success", success);
                                    Intent paymentResponseCallBackIntent = new Intent();
                                    paymentResponseCallBackIntent.putExtra(BasisPayPGConstants.PAYMENT_RESPONSE, pgResponse.toString());
                                    BasisPayPaymentActivity.this.setResult(-1, paymentResponseCallBackIntent);
                                    BasisPayPaymentActivity.this.finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                        }
                    });
                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    handler.proceed();
                    super.onReceivedSslError(view, handler, error);
                }
            });
            WebSettings webSettings = this.webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            this.webview.getSettings().setDomStorageEnabled(true);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setDomStorageEnabled(true);
            this.webview.clearHistory();
            this.webview.clearCache(true);
            this.webview.setWebChromeClient(new WebChromeClient() {
                public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                    return super.onJsAlert(view, url, message, result);
                }
            });
            String postUrl;
            if (isProduction) {
                //TODO LIVE MODE
                postUrl = BasisPayPGConstants.PRODUCTION_URL+BasisPayPGConstants.END_POINT;
                Log.d("Production PostUrl", postUrl);
            } else {
                //TODO TEST MODE
                postUrl = BasisPayPGConstants.STAGING_URL+BasisPayPGConstants.END_POINT;
                Log.d("Staging PostUrl", postUrl);
            }
            Log.d("postParamValues", postPaymentRequestParams);
            this.webview.postUrl(postUrl, postPaymentRequestParams.getBytes());

        } catch (Exception var7) {
            StringWriter sw = new StringWriter();
            var7.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Toast.makeText(this.getBaseContext(), exceptionAsString, Toast.LENGTH_SHORT).show();
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
        if (this.webview.getVisibility() == View.VISIBLE) {
            if (this.webview.canGoBack()) {
                this.webview.goBack();
            }

        } else {
            super.onBackPressed();
        }
    }

    public static Map<String, String> getQueryMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();

        for (String param : params) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }
}
