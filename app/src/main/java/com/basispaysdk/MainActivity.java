package com.basispaysdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

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
        pgPaymentParams.setApiKey("XXXXX");//required field(*)
        pgPaymentParams.setSecureHash("XXXXX");//required field(*)
        pgPaymentParams.setOrderReference("XXXXX");//required field(*)
        pgPaymentParams.setCustomerName("XXXXX");//required field(*)
        pgPaymentParams.setCustomerEmail("XXXXX");//required field(*)
        pgPaymentParams.setCustomerMobile("XXXXX");//required field(*)
        pgPaymentParams.setAddress("XXXXX");//required field(*)
        pgPaymentParams.setPostalCode("XXXXX");//required field(*)
        pgPaymentParams.setCity("XXXXX");//required field(*)
        pgPaymentParams.setRegion("XXXXX");//required field(*)
        pgPaymentParams.setCountry("XXX");//required field(*)

        //// optional parameters
        pgPaymentParams.setDeliveryAddress("XXXXX");
        pgPaymentParams.setDeliveryCustomerName("XXXXX");
        pgPaymentParams.setDeliveryCustomerMobile("XXXXX");
        pgPaymentParams.setDeliveryPostalCode("XXXXX");
        pgPaymentParams.setDeliveryCity("XXXXX");
        pgPaymentParams.setDeliveryRegion("XXXXX");
        pgPaymentParams.setDeliveryCountry("XXX");

        PaymentGatewayPaymentInitializer pgPaymentInitializer = new PaymentGatewayPaymentInitializer(pgPaymentParams,MainActivity.this);
        pgPaymentInitializer.initiatePaymentProcess();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PGConstants.REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){
                String paymentResponse=data.getStringExtra(PGConstants.PAYMENT_RESPONSE);
                System.out.println("paymentResponse: "+paymentResponse);
                if(paymentResponse.equals("null")){
                    System.out.println("Transaction Error!");
                    Toast.makeText(this,"Transaction Error!",Toast.LENGTH_SHORT).show();
                }else{
                    System.out.println("Payment: "+paymentResponse);
                    try {
                        JSONObject jsonObject = new JSONObject(paymentResponse);
                        if (jsonObject.getString("status").equals("success"))
                        {
                            System.out.println("Success");
                            Toast.makeText(this,"Transaction Success",Toast.LENGTH_SHORT).show();
                        }
                        else if (jsonObject.getString("status").equals("failed"))
                        {
                            System.out.println("Failure");
                            Toast.makeText(this,"Transaction Failed",Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                System.out.println("Transaction Canceled!!");
                Toast.makeText(this,"Transaction Canceled!",Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
