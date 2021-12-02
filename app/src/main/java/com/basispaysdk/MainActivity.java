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
        pgPaymentParams.setApiKey("0a12de78-c50b-46d6-96cf-ac42333ad0c5");//required field(*)
        pgPaymentParams.setSecureHash("svE0NWLXIpuoU5NC2isBAHTbP4T9N2ZiNg4RA5JG+L8=");//required field(*)
        pgPaymentParams.setCustomerName("XXXXX");//required field(*)
        pgPaymentParams.setCustomerEmail("XXXXX@gmail.com");//required field(*)
        pgPaymentParams.setCustomerMobile("9876543210");//required field(*)
        pgPaymentParams.setAddress("XXXXX");//required field(*)
        pgPaymentParams.setPostalCode("XXXXX");//required field(*)
        pgPaymentParams.setCity("XXXXX");//required field(*)
        pgPaymentParams.setRegion("XXXXX");//required field(*)
        pgPaymentParams.setCountry("XXXXX");//required field(*)

        //// optional parameters
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
                    //Completed or Failure
                    System.out.println("Payment Completed/Failure");
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
