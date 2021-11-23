# BasisPay-Android PG-KIT
BasisPay Android Payment Gateway kit for developers

## INTRODUCTION
This document describes the steps for integrating Basispay online payment gateway Android kit.This payment gateway performs the online payment transactions with less user effort. It receives the payment details as input and handles the payment flow. Finally returns the payment response to the user. User has to import the framework manually into their project for using it

## Code Explanation

Make sure you have the below permissions in your manifest file:
```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```
AndroidManifest.xml 
```

<?xml version="1.0" encoding="utf-8"?>
<manifest ...>
    <uses-permission android:name="android.permission.INTERNET" />
    <application
        ...
        android:usesCleartextTraffic="true"
	android:requestLegacyExternalStorage="true"
        ...>
        ...
    </application>
</manifest>

```

import com.basispaypg.PGConstants;
import com.basispaypg.PaymentGatewayPaymentInitializer;
import com.basispaypg.PaymentParams;

```
 PaymentParams pgPaymentParams = new PaymentParams();
        pgPaymentParams.setOrderReference("XXXXX*");
        pgPaymentParams.setCustomerName("XXXXX*");
        pgPaymentParams.setCustomerEmail("XXXXX*");
        pgPaymentParams.setCustomerMobile("XXXXX*");
        pgPaymentParams.setAddress("XXXXX*");
        pgPaymentParams.setPostalCode("XXXXX*");
        pgPaymentParams.setCity("XXXXX*");
        pgPaymentParams.setRegion("XXXXX*");
        pgPaymentParams.setCountry("XXXXX*");
        pgPaymentParams.setDeliveryAddress("XXXXX");
        pgPaymentParams.setDeliveryCustomerName("XXXXX");
        pgPaymentParams.setDeliveryCustomerMobile("XXXXX");
        pgPaymentParams.setDeliveryPostalCode("XXXXX");
        pgPaymentParams.setDeliveryCity("XXXXX");
        pgPaymentParams.setDeliveryRegion("XXXXX");
        pgPaymentParams.setDeliveryCountry("XXXXX");
   
```      
Initailize the com.basispaypg.PaymentGatewayPaymentInitializer class with payment parameters and initiate the payment:
```
PaymentGatewayPaymentInitializer pgPaymentInitialzer = new PaymentGatewayPaymentInitializer(pgPaymentParams,MainActivity.this);
pgPaymentInitialzer.initiatePaymentProcess();

```
## Payment Response
To receive the json response, override the onActivityResult() using the REQUEST_CODE and PAYMENT_RESPONSE variables from com.basispaypg.PaymentParams class
```
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

```
