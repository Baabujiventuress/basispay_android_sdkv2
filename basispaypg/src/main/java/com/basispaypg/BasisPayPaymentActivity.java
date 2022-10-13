package com.basispaypg;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

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
        System.out.println("ReturnUrl==="+returnUrl);

        try {
            this.webview.setWebViewClient(new WebViewClient() {
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    pb.setVisibility(View.GONE);
                    Log.i("log", "onPageFinished : " + url);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (url.contains("https://staging-connect.basispay.in/ui/response?")){
                                String[] s = url.split("https://staging-connect.basispay.in/ui/response");
                                Map<String, String> mapValue = getQueryMap(s[1]);
                                System.out.println(mapValue.get("?ref"));
                                System.out.println(mapValue.get("success"));
                                referenceNo = mapValue.get("?ref");
                                success = mapValue.get("success");
                                System.out.println("referenceNo=="+referenceNo);
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
                                    pgResponse.put("status", "success");
                                    pgResponse.put("payment_response", referenceNo);
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
            String postUrl = "https://staging-connect.basispay.in/checkout";
//            String postUrl = "https://connect.basispay.in/checkout";
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
                Log.d("TAG", "ResponseJson: " + jsonStringResponse);
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
                paymentResponseCallBackIntent.putExtra(BasisPayPGConstants.PAYMENT_RESPONSE, pgResponse.toString());
                BasisPayPaymentActivity.this.setResult(-1, paymentResponseCallBackIntent);
                BasisPayPaymentActivity.this.finish();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
