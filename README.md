# BasisPay-Android PG-KIT
BasisPay Android Payment Gateway kit for developers

## INTRODUCTION
This document describes the steps for integrating Basispay online payment gateway Android kit.This payment gateway performs the online payment transactions with less user effort. It receives the payment details as input and handles the payment flow. Finally returns the payment response to the user. User has to import the framework manually into their project for using it

## Add the JitPack repository to your build file
Step 1. Add the JitPack repository to your build file
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Step 2. Add the dependency
```
dependencies {
	        implementation 'com.github.Baabujiventuress:basispay_android_sdkv2:Tag'
	}
```

## Code Explanation

Make sure you have the below permissions in your manifest file:
```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```
import com.basispaypg.PGConstants;
import com.basispaypg.PaymentGatewayPaymentInitializer;
import com.basispaypg.PaymentParams;

```
 PaymentParams pgPaymentParams = new PaymentParams();
        pgPaymentParams.setApiKey("XXXXX");//required field(*)
	pgPaymentParams.setSecureHash("XXXXX");//required field(*)
        pgPaymentParams.setOrderReference("XXXXX");//required field(*)
        pgPaymentParams.setCustomerName("XXXXX");//required field(*)
        pgPaymentParams.setCustomerEmail("XXXXX@gmail.com");//required field(*)
        pgPaymentParams.setCustomerMobile("XXXXX");//required field(*)
        pgPaymentParams.setAddress("XXXXX");//required field(*)
        pgPaymentParams.setPostalCode("XXXXX");//required field(*)
        pgPaymentParams.setCity("XXXXX");//required field(*)
        pgPaymentParams.setRegion("XXXXX");//required field(*)
        pgPaymentParams.setCountry("XXXXX");//required field(*)
		
        //// optional parameters
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
                    //Completed or Failure
                    System.out.println("Payment Completed/Failure");
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                System.out.println("Transaction Canceled!!");
            }

        }
    }

```
