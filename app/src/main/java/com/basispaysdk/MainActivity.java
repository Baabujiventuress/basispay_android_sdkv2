package com.basispaysdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.basispaypg.PGConstants;
import com.basispaypg.PaymentGatewayPaymentInitializer;
import com.basispaypg.PaymentParams;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PaymentParams pgPaymentParams = new PaymentParams();
        pgPaymentParams.setOrderReference("123456");
        pgPaymentParams.setCustomerName("XXXX");
        pgPaymentParams.setCustomerEmail("XXXX@gmail.com");
        pgPaymentParams.setCustomerMobile("XXXXXXX");
        pgPaymentParams.setAddress("YYYYYYYY");
        pgPaymentParams.setPostalCode("ZZZZZZZZ");
        pgPaymentParams.setCity("XXXX");
        pgPaymentParams.setRegion("XXXX");
        pgPaymentParams.setCountry("XXXX");
        pgPaymentParams.setDeliveryAddress("XXXXXX");
        pgPaymentParams.setDeliveryCustomerName("YYYYYY");
        pgPaymentParams.setDeliveryCustomerMobile("ZZZZZZZZ");
        pgPaymentParams.setDeliveryPostalCode("ZZZZZZZZ");
        pgPaymentParams.setDeliveryCity("XXXXXX");
        pgPaymentParams.setDeliveryRegion("YYYYY");
        pgPaymentParams.setDeliveryCountry("XXXX");

        PaymentGatewayPaymentInitializer pgPaymentInitialzer = new PaymentGatewayPaymentInitializer(pgPaymentParams,MainActivity.this);
        pgPaymentInitialzer.initiatePaymentProcess();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PGConstants.REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){
                try{
                    String paymentResponse=data.getStringExtra(PGConstants.PAYMENT_RESPONSE);
                    System.out.println("paymentResponse: "+paymentResponse);
                    if(paymentResponse.equals("null")){
                        System.out.println("Transaction Error!");
                    }else{

                        JSONObject response = new JSONObject(paymentResponse);
                        if (response.get("statusCode") == "0") {

                        }else {

                        }

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }

        }
    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
