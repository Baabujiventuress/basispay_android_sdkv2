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

public class PaymentGatewayPaymentInitializer {
    private Activity context = null;
    private HashMap<String, String> params = new LinkedHashMap();

    public PaymentGatewayPaymentInitializer(PaymentParams paymentParams, Activity context) {
        this.context = context;
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
                                                        this.params.put("delivery[address]", paymentParams.getDeliveryAddress());
                                                        if (paymentParams.getDeliveryCustomerName() != null) {
                                                            this.params.put("delivery[customerName]", paymentParams.getDeliveryCustomerName());
                                                            if (paymentParams.getDeliveryCustomerMobile() != null) {
                                                                this.params.put("delivery[customerMobile]", paymentParams.getDeliveryCustomerMobile());
                                                                if (paymentParams.getDeliveryPostalCode() != null) {
                                                                    this.params.put("delivery[postalCode]", paymentParams.getDeliveryPostalCode());
                                                                    if (paymentParams.getDeliveryCity() != null) {
                                                                        this.params.put("delivery[city]", paymentParams.getDeliveryCity());
                                                                        if (paymentParams.getDeliveryRegion() != null) {
                                                                            this.params.put("delivery[region]", paymentParams.getDeliveryRegion());
                                                                            if (paymentParams.getDeliveryCountry() != null) {
                                                                                this.params.put("delivery[country]", paymentParams.getDeliveryCountry());
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

    public void initiatePaymentProcess() {
        Intent startActivity = new Intent(this.context, PaymentGatewayPaymentActivity.class);
        startActivity.putExtra(PGConstants.POST_PARAMS, this.buildParamsForPayment());
        startActivity.setFlags(8388608);
        this.context.startActivityForResult(startActivity, PGConstants.REQUEST_CODE);
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
            Toast.makeText(this.context, parameterEntry, 0).show();
        }

        String postParams = hashPostParamsBuilder.toString();
        return postParams.charAt(postParams.length() - 1) == '&' ? postParams.substring(0, postParams.length() - 1) : postParams;
    }
}
