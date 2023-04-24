package com.basispaysdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.basispaypg.BasisPayPGConstants;
import com.basispaypg.BasisPayPaymentInitializer;
import com.basispaypg.BasisPayPaymentParams;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BasisPayPaymentParams pgPaymentParams = new BasisPayPaymentParams();
        pgPaymentParams.setApiKey(Const.PG_API_KEY);//required field(*)
        pgPaymentParams.setSecureHash(Const.PG_SECURE_HASH);//required field(*)
        pgPaymentParams.setOrderReference(Const.PG_REFERENCE);//required field(*)
        pgPaymentParams.setCustomerName(Const.PG_USER_NAME);//required field(*)
        pgPaymentParams.setCustomerEmail(Const.PG_USER_EMAIL);//required field(*)
        pgPaymentParams.setCustomerMobile(Const.PG_USER_MOBILE);//required field(*)
        pgPaymentParams.setAddress(Const.PG_ADDRESS);//required field(*)
        pgPaymentParams.setPostalCode(Const.PG_PINCODE);//required field(*)
        pgPaymentParams.setCity(Const.PG_CITY);//required field(*)
        pgPaymentParams.setRegion(Const.PG_REGION);//required field(*)
        pgPaymentParams.setCountry(Const.PG_COUNTRY);//required field(*)

        //// optional parameters
        pgPaymentParams.setDeliveryAddress("");
        pgPaymentParams.setDeliveryCustomerName("");
        pgPaymentParams.setDeliveryCustomerMobile("");
        pgPaymentParams.setDeliveryPostalCode("");
        pgPaymentParams.setDeliveryCity("");
        pgPaymentParams.setDeliveryRegion("");
        pgPaymentParams.setDeliveryCountry(Const.PG_COUNTRY);

        BasisPayPaymentInitializer pgPaymentInitializer = new BasisPayPaymentInitializer(pgPaymentParams,MainActivity.this,
                Const.PG_RETURN_URL,false); //LIVE= true or TEST= false
        pgPaymentInitializer.initiatePaymentProcess();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BasisPayPGConstants.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    String paymentResponse = data.getStringExtra(BasisPayPGConstants.PAYMENT_RESPONSE);
                    System.out.println(paymentResponse);
                    Log.d("Res",paymentResponse);
                    if (paymentResponse.equals("null")) {
                        Toast.makeText(this, "Transaction Error!", Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject response = new JSONObject(paymentResponse);
                        Log.d("Res", response.toString());
                        String referenceNo = response.getString("referenceNo");
                        boolean success = response.getBoolean("success");

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }

        }
    }
}
