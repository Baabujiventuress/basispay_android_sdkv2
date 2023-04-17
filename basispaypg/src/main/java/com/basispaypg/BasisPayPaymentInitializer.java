package com.basispaypg;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.TreeMap;

public class BasisPayPaymentInitializer {
    private final Activity context;
    private final HashMap<String, String> params = new LinkedHashMap();
    private final String returnUrl;
    private final String paymentUrl;

    public BasisPayPaymentInitializer(BasisPayPaymentParams paymentParams, Activity context,
                                      String returnUrl, String payUrl) {
        this.context = context;
        this.returnUrl = returnUrl;
        this.paymentUrl = payUrl;

        if (TextUtils.isEmpty(paymentParams.getApiKey())) {
            throw new RuntimeException("ApiKey missing");
        }else {
            this.params.put("apiKey", paymentParams.getApiKey());
            if (TextUtils.isEmpty(paymentParams.getSecureHash())) {
                throw new RuntimeException("SecureHash missing");
            }else {
                this.params.put("secureHash", paymentParams.getSecureHash());
                if (TextUtils.isEmpty(paymentParams.getOrderReference())) {
                    throw new RuntimeException("Order Reference missing");
                } else {
                    this.params.put("orderReference", paymentParams.getOrderReference());
                    if (TextUtils.isEmpty(paymentParams.getCustomerName())) {
                        throw new RuntimeException("Customer Name missing");
                    } else {
                        this.params.put("customerName", paymentParams.getCustomerName());
                        if (TextUtils.isEmpty(paymentParams.getCustomerEmail())) {
                            throw new RuntimeException("customer Email missing");
                        } else {
                            this.params.put("customerEmail", paymentParams.getCustomerEmail());
                            if (TextUtils.isEmpty(paymentParams.getCustomerMobile())) {
                                throw new RuntimeException("customerMobile missing");
                            } else {
                                this.params.put("customerMobile", paymentParams.getCustomerMobile());
                                if (TextUtils.isEmpty(paymentParams.getAddress())) {
                                    throw new RuntimeException("address missing");
                                } else {
                                    this.params.put("address", paymentParams.getAddress());
                                    if (TextUtils.isEmpty(paymentParams.getPostalCode())) {
                                        throw new RuntimeException("postalCode missing");
                                    } else {
                                        this.params.put("postalCode", paymentParams.getPostalCode());
                                        if (TextUtils.isEmpty(paymentParams.getCity())) {
                                            throw new RuntimeException("city missing");
                                        } else {
                                            this.params.put("city", paymentParams.getCity());
                                            if (TextUtils.isEmpty(paymentParams.getRegion())) {
                                                throw new RuntimeException("region missing");
                                            } else {
                                                this.params.put("region", paymentParams.getRegion());
                                                if (TextUtils.isEmpty(paymentParams.getCity())) {
                                                    throw new RuntimeException("City missing");
                                                } else {
                                                    this.params.put("city", paymentParams.getCity());
                                                    if (TextUtils.isEmpty(paymentParams.getCountry())) {
                                                        throw new RuntimeException("country missing");
                                                    } else {
                                                        this.params.put("country", paymentParams.getCountry());
                                                        if (paymentParams.getDeliveryAddress() != null) {
                                                            this.params.put("deliveryAddress", paymentParams.getDeliveryAddress());
                                                            if (paymentParams.getDeliveryCustomerName() != null) {
                                                                this.params.put("deliveryName", paymentParams.getDeliveryCustomerName());
                                                                if (paymentParams.getDeliveryCustomerMobile() != null) {
                                                                    this.params.put("deliveryMobile", paymentParams.getDeliveryCustomerMobile());
                                                                    if (paymentParams.getDeliveryPostalCode() != null) {
                                                                        this.params.put("deliveryPostalCode", paymentParams.getDeliveryPostalCode());
                                                                        if (paymentParams.getDeliveryCity() != null) {
                                                                            this.params.put("deliveryCity", paymentParams.getDeliveryCity());
                                                                            if (paymentParams.getDeliveryRegion() != null) {
                                                                                this.params.put("deliveryRegion", paymentParams.getDeliveryRegion());
                                                                                if (paymentParams.getDeliveryCountry() != null) {
                                                                                    this.params.put("deliveryCountry", paymentParams.getDeliveryCountry());
                                                                                } else {
                                                                                    throw new RuntimeException("delivery country missing");
                                                                                }
                                                                            } else {
                                                                                throw new RuntimeException("delivery region missing");
                                                                            }
                                                                        } else {
                                                                            throw new RuntimeException("delivery city missing");
                                                                        }
                                                                    } else {
                                                                        throw new RuntimeException("delivery postalCode missing");
                                                                    }

                                                                } else {
                                                                    throw new RuntimeException("delivery customerMobile missing");
                                                                }
                                                            } else {
                                                                throw new RuntimeException("delivery customerName missing");
                                                            }

                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }

                }
            }
        }

    }

    public void initiatePaymentProcess() {
        Intent startActivity = new Intent(this.context, BasisPayPaymentActivity.class);
        startActivity.putExtra(BasisPayPGConstants.POST_PARAMS, this.buildParamsForPayment());
        startActivity.putExtra(BasisPayPGConstants.PAYMENT_URL, this.paymentUrl);
        startActivity.putExtra(BasisPayPGConstants.PAYMENT_RETURN_URL, this.returnUrl);
        startActivity.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        this.context.startActivityForResult(startActivity, BasisPayPGConstants.REQUEST_CODE);
    }

    private String buildParamsForPayment() {
        HashMap<String, String> params = this.params;
        StringBuilder hashPostParamsBuilder = new StringBuilder();
        String parameterEntry;
        TreeMap<String, String> sorted = new TreeMap<>();

        // Copy all data from hashMap into TreeMap
        sorted.putAll(params);

        // Display the TreeMap which is naturally sorted
        try {
            for(Iterator var3 = sorted.keySet().iterator(); var3.hasNext(); hashPostParamsBuilder = hashPostParamsBuilder.append(parameterEntry)) {
                String key = (String)var3.next();
                parameterEntry = key + "=" + (String)sorted.get(key) + "&";
            }
        } catch (Exception var6) {
            StringWriter sw = new StringWriter();
            var6.printStackTrace(new PrintWriter(sw));
            parameterEntry = sw.toString();
            Toast.makeText(this.context, parameterEntry, Toast.LENGTH_SHORT).show();
        }

        String postParams = hashPostParamsBuilder.toString();
        return postParams.charAt(postParams.length() - 1) == '&' ? postParams.substring(0, postParams.length() - 1) : postParams;
    }
}
