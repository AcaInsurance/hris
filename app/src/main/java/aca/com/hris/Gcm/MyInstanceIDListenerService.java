package aca.com.hris.Gcm;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.HashMap;

import aca.com.hris.Database.GcmMapping;
import aca.com.hris.Database.User;
import aca.com.hris.HelperClass.GcmDal;
import aca.com.hris.HelperClass.WebServicesNonQuery;
import aca.com.hris.R;
import rx.Observer;

/**
 * Created by Marsel on 28/6/2016.
 */
public class MyInstanceIDListenerService extends FirebaseInstanceIdService {

    private static final String TAG = "MyInstanceIDLS";

    @Override
    public void onTokenRefresh() {

        try {

            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.d(TAG, "Refreshed token: " + refreshedToken);
            saveRegistrationToken(refreshedToken);
            GcmDal.sendRegistrationToken(getApplicationContext());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void saveRegistrationToken(String token) {
        GcmMapping gcmMapping;

        try {
            gcmMapping = new Select().from(GcmMapping.class).querySingle();

            if (gcmMapping == null) {
                gcmMapping = new GcmMapping();
            }

            gcmMapping.IdKaryawan = User.getEmpCode();
            gcmMapping.RegisteredToken = token;
            gcmMapping.save();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Observer<? super GcmMapping> registrationTokenObserver() {
        return new Observer<GcmMapping>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                doOnError();
            }

            @Override
            public void onNext(GcmMapping gcmMapping) {
                try {
                    if (gcmMapping == null) {
                        doOnError();
                        return;
                    }

                    Delete.table(GcmMapping.class);
                    gcmMapping.save();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void doOnError() {
//                Toast.makeText(getBaseContext(), R.string.messasge_failed_register_token, Toast.LENGTH_SHORT).show();
            }
        };
    }
}
