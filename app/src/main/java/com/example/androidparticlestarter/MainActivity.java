package com.example.androidparticlestarter;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.utils.Async;

public class MainActivity extends AppCompatActivity {
    private TextView Result;

    String comp ;
    // MARK: Debug info
    private final String TAG="SMARTLIGHT";

    // MARK: Particle Account Info
    private final String PARTICLE_USERNAME = "usivaprasad95@gmail.com";
    private final String PARTICLE_PASSWORD = "Sivaprasad@@95";

    // MARK: Particle device-specific info
    private final String DEVICE_ID = "300023001047363333343437";

    // MARK: Particle Publish / Subscribe variables
    private long subscriptionId;

    // MARK: Particle device
    private ParticleDevice mDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Result = (TextView) findViewById(R.id.outputTV);

        // 1. Initialize your connection to the Particle API
        ParticleCloudSDK.init(this.getApplicationContext());

        // 2. Setup your device variable
        getDeviceFromCloud();

    }

    public void getVoiceInputHere(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 1);
        }
        else {
            Toast.makeText(this, "Does not support", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Custom function to connect to the Particle Cloud and get the device
     */
    public void getDeviceFromCloud() {
        // This function runs in the background
        // It tries to connect to the Particle Cloud and get your device
        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {

            @Override
            public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                particleCloud.logIn(PARTICLE_USERNAME, PARTICLE_PASSWORD);
                mDevice = particleCloud.getDevice(DEVICE_ID);
                return -1;
            }

            @Override
            public void onSuccess(Object o) {

                Log.d(TAG, "Successfully got device from Cloud");
            }

            @Override
            public void onFailure(ParticleCloudException exception) {
                Log.d(TAG, exception.getBestMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {
            @Override
            public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                // put your logic here to talk to the particle
                // --------------------------------------------

                // what functions are "public" on the particle?
                Log.d(TAG, "Availble functions: " + mDevice.getFunctions());

                switch (requestCode) {
                    case 1:

                        if (resultCode == RESULT_OK && data != null) {
                            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                            try{
                                Result.setText(result.get(0));
                            }

                            catch (Exception e){
                                Log.d(TAG, "Did not used API");
                            }

                            if ((Result.getText().toString().equals("on"))
                                    || (Result.getText().toString().equals("turn lights on"))
                                    || (Result.getText().toString().equals("lights on"))
                                    || (Result.getText().toString().equals("turn leds on"))) {
                                try{
                                    mDevice.callFunction("lightsOn", result);
                                }
                                catch (ParticleDevice.FunctionDoesNotExistException e1) {
                                    e1.printStackTrace();
                                }
                            }
                            else  if ((Result.getText().toString().equals("off"))
                                    || (Result.getText().toString().equals("turn lights off"))
                                    || (Result.getText().toString().equals("lights off"))
                                    || (Result.getText().toString().equals("turn leds off"))) {
                                try {
                                    mDevice.callFunction("lightsOff", result);
                                }
                                catch (ParticleDevice.FunctionDoesNotExistException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        break;
                }
                return -1;
            }

            @Override
            public void onSuccess(Object o) {
                // put your success message here
                Log.d(TAG, "Success!");
            }

            @Override
            public void onFailure(ParticleCloudException exception) {
                // put your error handling code here
                Log.d(TAG, exception.getBestMessage());
            }
        });

    }

//    public void turnLightsOffPressed(View view) {
//        Toast.makeText(getApplicationContext(), "Off pressed", Toast.LENGTH_SHORT)
//                .show();
//
//
//
//        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {
//            @Override
//            public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {
//                // put your logic here to talk to the particle
//                // --------------------------------------------
//
//                // what functions are "public" on the particle?
//                Log.d(TAG, "Availble functions: " + mDevice.getFunctions());
//
//
//                List<String> functionParameters = new ArrayList<String>();
//                //functionParameters.add();
//                try {
//                    mDevice.callFunction("turnLightsOff", functionParameters);
//
//                } catch (ParticleDevice.FunctionDoesNotExistException e1) {
//                    e1.printStackTrace();
//                }
//
//
//                return -1;
//            }
//
//            @Override
//            public void onSuccess(Object o) {
//                // put your success message here
//                Log.d(TAG, "Success!");
//            }
//
//            @Override
//            public void onFailure(ParticleCloudException exception) {
//                // put your error handling code here
//                Log.d(TAG, exception.getBestMessage());
//            }
//        });
//
//
//
//    }
}


