package com.basispaypg;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
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

/**
 * @author Devops Team Vinoth
 * Published By BasisPay PG Version2
 */
public class BasisPayPaymentActivity extends AppCompatActivity {
    ProgressBar pb;
    WebView webview;
    String referenceNo, success;

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
        boolean isProduction = this.getIntent().getBooleanExtra(BasisPayPGConstants.IS_PRODUCTION, false);

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
                                checkUrl = BasisPayPGConstants.PRODUCTION_URL + BasisPayPGConstants.CONTAIN_CHECK;
                                appUrl = BasisPayPGConstants.PRODUCTION_URL + BasisPayPGConstants.CONTAIN_RES;
                            } else { //TODO TEST MODE
                                checkUrl = BasisPayPGConstants.STAGING_URL + BasisPayPGConstants.CONTAIN_CHECK;
                                appUrl = BasisPayPGConstants.STAGING_URL + BasisPayPGConstants.CONTAIN_RES;
                            }
                            if (url.contains(checkUrl)) {
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
                    pb.setVisibility(View.GONE);
                    Log.i("log", "onPageStarted : " + url);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject pgResponse = new JSONObject();
                            if (url.equalsIgnoreCase(returnUrl)) {
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
                    final AlertDialog.Builder builder = new AlertDialog.Builder(BasisPayPaymentActivity.this);
                    String message;
                    switch (error.getPrimaryError()) {
                        case SslError.SSL_EXPIRED:
                            message = "The certificate has expired.";
                            break;
                        case SslError.SSL_IDMISMATCH:
                            message = "The certificate Hostname mismatch.";
                            break;
                        case SslError.SSL_UNTRUSTED:
                            message = "The certificate authority is not trusted.";
                            break;
                        case SslError.SSL_DATE_INVALID:
                            message = "The certificate date is invalid.";
                            break;
                        case SslError.SSL_NOTYETVALID:
                            message = "The certificate is not yet valid.";
                            break;
                        default:
                            message = "Unknown SSL error.";
                            break;
                    }

                    builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            handler.proceed();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            handler.cancel();
                            //Cancel Transaction
                            cancelTransaction();
                        }
                    });
                    message += " Do you want to continue anyway?";
                    builder.setTitle("Transaction Alert");
                    builder.setMessage("Do you want to proceed transaction?");
                    final AlertDialog dialog = builder.create();
                    dialog.show();
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    String url = request.getUrl().toString();
                    if (url.startsWith("http://")) {
                        try {
                            //change protocol of url string
                            url = url.replace("http://", "https://");
                            view.loadUrl(url);
                            return super.shouldOverrideUrlLoading(view, request);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("TAG", e.toString());
                        }
                    }
                    if (url.startsWith("upi:")) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            startActivity(intent);
                            return true;
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                            Log.d("TAG", e.toString());
                            Toast.makeText(BasisPayPaymentActivity.this,
                                    "UPI app were not found!", Toast.LENGTH_SHORT).show();
                            final AlertDialog.Builder builder = new AlertDialog.Builder(BasisPayPaymentActivity.this);
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //Cancel Transaction
                                    cancelTransaction();
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //Cancel Transaction
                                    cancelTransaction();
                                }
                            });
                            builder.setTitle("Alert");
                            builder.setMessage("UPI apps were not found this device! Do you want to cancel transaction?");
                            final AlertDialog dialog = builder.create();
                            dialog.setCancelable(false);
                            dialog.show();
                        }
                    }
                    return false;
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
                postUrl = BasisPayPGConstants.PRODUCTION_URL + BasisPayPGConstants.END_POINT;
                Log.d("Production PostUrl", postUrl);
            } else {
                //TODO TEST MODE
                postUrl = BasisPayPGConstants.STAGING_URL + BasisPayPGConstants.END_POINT;
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
            switch (keyCode) {
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

    public void cancelTransaction() {
        try {
            JSONObject pgResponse = new JSONObject();
            pgResponse.put("referenceNo", "TRANSACTION CANCELLED!");
            pgResponse.put("success", "false");
            Intent paymentResponseCallBackIntent = new Intent();
            paymentResponseCallBackIntent.putExtra(BasisPayPGConstants.PAYMENT_RESPONSE, pgResponse.toString());
            setResult(-1, paymentResponseCallBackIntent);
            finish();
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("TAG:", ex.toString());
            System.out.println(ex.getMessage());
        }
    }
}
