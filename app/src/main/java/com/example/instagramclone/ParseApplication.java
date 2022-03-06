package com.example.instagramclone;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

// Create a ParseApplication for Backend communication
public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Register Post model
        ParseObject.registerSubclass(Post.class);

        // Initialize the Parse model
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("AKPYzsFrGGoyPKdQ1xREEnAr69HV2XcpoMzKKo5o")
                .clientKey("ubpIQ073uM80uwWKf5QKYTl72pptl3pTIThdj8po")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
