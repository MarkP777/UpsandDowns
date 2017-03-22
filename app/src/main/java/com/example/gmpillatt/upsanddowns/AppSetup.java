package com.example.gmpillatt.upsanddowns;

import android.app.Application;
import android.os.StrictMode;

/**
 * Created by gmpillatt on 21/03/2017.
 */

public class AppSetup extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Code added to get round FileUriExposedException in API 24 and above.
        //See http://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

    }
}
