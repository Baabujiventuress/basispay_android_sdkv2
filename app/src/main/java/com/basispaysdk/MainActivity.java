package com.basispaysdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.basispaypg.PGConstants;
import com.basispaypg.PaymentGatewayPaymentInitializer;
import com.basispaypg.PaymentParams;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PaymentParams pgPaymentParams = new PaymentParams();
        pgPaymentParams.setOrderReference("H_rvZ72qK74BhLlMvJbwjQ==");
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

    @SuppressLint("MissingSuperCall")
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
