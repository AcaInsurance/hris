package aca.com.hris;

import android.app.Application;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by Marsel on 2/11/2015.
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(getApplicationContext());
    }

}
