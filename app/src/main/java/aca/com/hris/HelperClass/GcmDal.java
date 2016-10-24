package aca.com.hris.HelperClass;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.HashMap;

import aca.com.hris.Database.GcmMapping;
import aca.com.hris.Database.User;
import aca.com.hris.R;
import aca.com.hris.Retrofit.HrisService;
import aca.com.hris.Util.Var;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by marsel on 19/8/2016.
 */
public class GcmDal {

    public static void sendRegistrationToken(final Context context) {
        String token = "";
        GcmMapping gcmMapping = new Select().from(GcmMapping.class).querySingle();
        if (gcmMapping != null)
            token = gcmMapping.RegisteredToken;


        HashMap<String, String> map = new HashMap();
        map.put("registeredToken", token);
        map.put("idKaryawan", User.getEmpCode());
        map.put("isLogin", gcmMapping.IsLogin);

        WebServicesNonQuery ws = new WebServicesNonQuery(
                null,
                "RegisterToken",
                map,
                context.getResources().getStringArray(R.array.RegisterToken_post),
                context.getResources().getStringArray(R.array.RegisterToken_get),
                true
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                try {
                    String result = arrList.get(0).get(context.getResources().getStringArray(R.array.RegisterToken_get)[0]);
                    if (result.equalsIgnoreCase(String.valueOf(true))) {

                    } else {
                        Toast.makeText(context, "Failed to register token", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onFailed(String message) {
                try {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onCancel() {

            }
        };
        ws.execute();
    }



    private static RequestBody getBody() {
        GcmMapping gcmMapping = new Select().from(GcmMapping.class).querySingle();
        if (gcmMapping == null)
            return null;

        RequestBody requestBody;
        requestBody = new FormBody.Builder()
                .add("registeredToken", gcmMapping.RegisteredToken)
                .build();
        return requestBody;
    }

    public static void deleteToken() {
        HrisService.createHrisService(getBody())
                .DeleteToken("DeleteToken")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {
                        Log.d("gcm delete", "success");
                        Delete.table(GcmMapping.class);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.d("gcm delete", "gagal");
                    }

                    @Override
                    public void onNext(Void aVoid) {

                    }
                });
    }
}
