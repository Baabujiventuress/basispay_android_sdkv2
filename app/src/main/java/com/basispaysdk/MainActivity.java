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
        pgPaymentParams.setOrderReference("0Ea4n_fHa-RDL_vIOcJ5Dw==");
        pgPaymentParams.setCustomerName("XYXYXY");
        pgPaymentParams.setCustomerEmail("YXYXYX@gmail.com");
        pgPaymentParams.setCustomerMobile("9876543210");
        pgPaymentParams.setAddress("ZZZZZZXXXXXXX");
        pgPaymentParams.setPostalCode("ZZZXXX");
        pgPaymentParams.setCity("XXXXX");
        pgPaymentParams.setRegion("YYYYYY");
        pgPaymentParams.setCountry("ZZZZZZ");
        pgPaymentParams.setDeliveryAddress("XXXXX");
        pgPaymentParams.setDeliveryCustomerName("XXXXX");
        pgPaymentParams.setDeliveryCustomerMobile("9876543210");
        pgPaymentParams.setDeliveryPostalCode("XXXXX");
        pgPaymentParams.setDeliveryCity("XXXXX");
        pgPaymentParams.setDeliveryRegion("XXXXX");
        pgPaymentParams.setDeliveryCountry("XXXXX");

        PaymentGatewayPaymentInitializer pgPaymentInitialzer = new PaymentGatewayPaymentInitializer(pgPaymentParams,MainActivity.this);
        pgPaymentInitialzer.initiatePaymentProcess();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PGConstants.REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){
                String paymentResponse=data.getStringExtra(PGConstants.PAYMENT_RESPONSE);
                System.out.println("paymentResponse: "+paymentResponse);
                if(paymentResponse.equals("null")){
                    System.out.println("Transaction Error!");
                }else{
                    System.out.println("Transaction Completed!");
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                System.out.println("Transaction Canceled!!");
            }

        }
    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
